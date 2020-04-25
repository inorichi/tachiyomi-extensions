package eu.kanade.tachiyomi.extension.ko.mangashowme

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Application
import android.content.SharedPreferences
import android.os.Build
import android.support.v7.preference.CheckBoxPreference
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.PreferenceScreen
import android.widget.Toast
import eu.kanade.tachiyomi.extension.BuildConfig
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.asObservableSuccess
import eu.kanade.tachiyomi.source.ConfigurableSource
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.util.asJsoup
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import rx.Observable
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * ManaMoa Source
 *
 * Originally it was mangashow.me extension but they changed site structure widely.
 * so I moved to new name for treating as new source.
 *  Users who uses =<1.2.11 need to migrate source. starts 1.2.12
 *
 * PS. There's no Popular section. It's just a list of manga. Also not latest updates.
 *     `manga_list` returns latest 'added' manga. not a chapter updates.
 **/
class ManaMoa : ConfigurableSource, ParsedHttpSource() {

    override val name = "ManaMoa"

    // This keeps updating: https://twitter.com/manamoa24
    private val defaultBaseUrl = "https://manamoa34.net"
    override val baseUrl by lazy { getCurrentBaseUrl() }

    override val lang: String = "ko"

    // Latest updates currently returns duplicate manga as it separates manga into chapters
    // But allowing to fetch from chapters with experimental setting.
    override val supportsLatest by lazy { getExperimentLatest() }

    override val client: OkHttpClient = network.cloudflareClient.newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(ImageDecoderInterceptor())
        .addInterceptor(ImageUrlHandlerInterceptor())
        .build()!!

    override fun popularMangaSelector() = "div.manga-list-gallery > div > div.post-row"

    override fun popularMangaFromElement(element: Element): SManga {
        val linkElement = element.select("a")
        val titleElement = element.select(".manga-subject > a").first()

        val manga = SManga.create()
        manga.url = linkElement.attr("href")
        manga.title = titleElement.html().trim()
        manga.thumbnail_url = urlFinder(element.select(".img-wrap-back").attr("style"))
        return manga
    }

    override fun popularMangaNextPageSelector() = "ul.pagination > li:not(.disabled)"

    // Do not add page parameter if page is 1 to prevent tracking.
    override fun popularMangaRequest(page: Int) = GET("$baseUrl/bbs/page.php?hid=manga_list" +
            if (page > 1) "&page=${page - 1}" else "")

    override fun popularMangaParse(response: Response): MangasPage {
        val document = response.asJsoup()

        val mangas = document.select(popularMangaSelector()).map { element ->
            popularMangaFromElement(element)
        }

        val hasNextPage = try {
            !document.select(popularMangaNextPageSelector()).last().hasClass("active")
        } catch (_: Exception) {
            false
        }

        return MangasPage(mangas, hasNextPage)
    }

    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector() = popularMangaSelector()
    override fun searchMangaParse(response: Response) = popularMangaParse(response)
    override fun fetchSearchManga(page: Int, query: String, filters: FilterList): Observable<MangasPage> {
        return if (query.startsWith(PREFIX_ID_SEARCH)) {
            val realQuery = query.removePrefix(PREFIX_ID_SEARCH)
            val urlPath = "/bbs/page.php?hid=manga_detail&manga_id=$realQuery"
            client.newCall(GET("$baseUrl$urlPath"))
                .asObservableSuccess()
                .map { response ->
                    val details = mangaDetailsParse(response)
                    details.url = urlPath
                    MangasPage(listOf(details), false)
                }
        } else super.fetchSearchManga(page, query, filters)
    }

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request =
        searchComplexFilterMangaRequestBuilder(baseUrl, page, query, filters)

    override fun mangaDetailsParse(document: Document): SManga {
        val info = document.select("div.left-info").first()
        val thumbnailElement = info.select("div.manga-thumbnail").first()
        val publishTypeText = thumbnailElement.select("a.publish_type").trimText("Unknown")
        val authorText = thumbnailElement.select("a.author").trimText()

        val mangaStatus = info.select("div.recommend")
        val mangaLike = mangaStatus.select(".fa-thumbs-up").trimText("0")
        // val mangaViews = trimElementText(mangaStatus.select(".fa-smile-o"), "0")
        val mangaComments = mangaStatus.select(".fa-comment").trimText("0")
        val mangaBookmarks = info.select(".fa-bookmark").trimText("0")
        val mangaChaptersLike = mangaElementsSum(document.select(".title i.fa.fa-thumbs-up > span"))
        val mangaChaptersComments = mangaElementsSum(document.select(".title i.fa.fa-comment > span"))

        val genres = mutableListOf<String>()
        document.select("div.left-info div.information > .manga-tags > a.tag").forEach {
            genres.add(it.text())
        }

        val manga = SManga.create()
        manga.title = info.select("div.red.title").html().trim()
        // They using background-image style tag for cover. extract url from style attribute.
        manga.thumbnail_url = urlFinder(thumbnailElement.attr("style"))
        manga.description =
            "\uD83D\uDCDD: $publishTypeText\n" +
                "👍: $mangaLike ($mangaChaptersLike)\n" +
                // "\uD83D\uDD0D: $mangaViews\n" +
                "\uD83D\uDCAC: $mangaComments ($mangaChaptersComments)\n" +
                "\uD83D\uDD16: $mangaBookmarks"
        manga.author = authorText
        manga.genre = genres.joinToString(", ")
        manga.status = parseStatus(publishTypeText)
        return manga
    }

    private fun parseStatus(status: String) = when (status.trim()) {
        "주간", "격주", "월간", "격월/비정기", "단행본" -> SManga.ONGOING
        "단편", "완결" -> SManga.COMPLETED
        else -> SManga.UNKNOWN
    }

    private fun mangaElementsSum(element: Elements?): String {
        if (element.isNullOrEmpty()) return "0"
        return try {
            String.format("%,d", element.map {
                it.text().toInt()
            }.sum())
        } catch (_: Exception) {
            "0"
        }
    }

    override fun chapterListSelector() = "div.manga-detail-list > div.chapter-list > .slot"

    override fun chapterFromElement(element: Element): SChapter {
        val linkElement = element.select("a")
        val rawName = linkElement.select("div.title").last()

        val chapter = SChapter.create()
        chapter.setUrlWithoutDomain(linkElement.attr("href"))
        chapter.chapter_number = parseChapterNumber(rawName.text())
        chapter.name = rawName.html().substringBefore("<span").trim()
        chapter.date_upload = parseChapterDate(element.select("div.addedAt").text().split(" ").first())
        return chapter
    }

    private fun parseChapterNumber(name: String): Float {
        try {
            if (name.endsWith("단편")) return 1f
            // `특별` means `Special`, so It can be buggy. so pad `편`(Chapter) to prevent false return
            if (name.contains("번외") || name.contains("특별편")) return -2f
            val regex = Regex("([0-9]+)(?:[-.]([0-9]+))?(?:화)")
            val (ch_primal, ch_second) = regex.find(name)!!.destructured
            return (ch_primal + if (ch_second.isBlank()) "" else ".$ch_second").toFloatOrNull() ?: -1f
        } catch (e: Exception) {
            e.printStackTrace()
            return -1f
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseChapterDate(date: String): Long {
        val calendar = Calendar.getInstance()

        // MangaShow.Me doesn't provide uploaded year now(18/12/15).
        // If received month is bigger then current month, set last year.
        // TODO: Fix years due to lack of info.
        return try {
            val month = date.trim().split('-').first().toInt()
            val currYear = calendar.get(Calendar.YEAR)
            val year = if (month > calendar.get(Calendar.MONTH) + 1) // Before December now, // and Retrieved month is December == 2018.
                currYear - 1 else currYear
            SimpleDateFormat("yyyy-MM-dd").parse("$year-$date").time
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    // They are using full url in every links.
    // There's possibility to using another domain for serve manga(s). Like marumaru.
    // override fun pageListRequest(chapter: SChapter) = GET(chapter.url, headers)
    override fun pageListParse(document: Document): List<Page> {
        val pages = mutableListOf<Page>()

        try {
            val element = document.toString()
            val cdnHandler = MMCDNUrlHandler(element)

            val imageUrl = element.substringBetween("var img_list = [", "];")
            val imageUrls = cdnHandler.replace(JSONArray("[$imageUrl]"))

            val imageUrl1 = element.substringBetween("var img_list1 = [", "];")
            val imageUrls1 = cdnHandler.replace(JSONArray("[$imageUrl1]"))

            val decoder = ImageDecoder(element)

            (imageUrls.indices)
                .map {
                    imageUrls[it] + try {
                        "!!${imageUrls1[it]}"
                    } catch (_: Exception) {
                        ""
                    }
                }
                .forEach { pages.add(Page(pages.size, decoder.request(it), "${it.substringBefore("!!")}")) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pages
    }

    override fun imageRequest(page: Page): Request {
        val requestHeaders = try {
            val data = page.url.substringAfter("??", "")
            val secondUrl = page.url.substringAfter("!!", "").substringBefore("??")

            val builder = headers.newBuilder()!!

            if (data.isNotBlank()) {
                builder.add("ImageDecodeRequest", data)
            }

            if (secondUrl.isNotBlank()) {
                builder.add("SecondUrlToRequest", secondUrl)
            }

            builder.build()!!
        } catch (_: Exception) {
            headers
        }.newBuilder()!!.add("ImageRequest", "1").build()!!

        return GET(page.imageUrl!!, requestHeaders)
    }

    // Latest not supported
    override fun latestUpdatesSelector() = ".post-row > div.media.post-list"

    override fun latestUpdatesFromElement(element: Element): SManga {
        val linkElement = element.select("a.btn-primary")
        val rawTitle = element.select(".post-subject > a").first().ownText()

        // TODO: Make Clear Regex.
        val chapterRegex = Regex("""((?:\s+)(?:(?:(?:[0-9]+권)?(?:[0-9]+부)?(?:[0-9]*?시즌[0-9]*?)?)?(?:\s*)(?:(?:[0-9]+)(?:[-.](?:[0-9]+))?)?(?:\s*[~,]\s*)?(?:[0-9]+)(?:[-.](?:[0-9]+))?)(?:화))""")
        val title = rawTitle.trim().replace(chapterRegex, "")
        // val regexSpecialChapter = Regex("(부록|단편|외전|.+편)")
        // val lastTitleWord = excludeChapterTitle.split(" ").last()
        // val title = excludeChapterTitle.replace(lastTitleWord, lastTitleWord.replace(regexSpecialChapter, ""))

        val manga = SManga.create()
        manga.url = linkElement.attr("href")
        manga.title = title
        manga.thumbnail_url = element.select(".img-item > img").attr("src")
        manga.initialized = false
        return manga
    }

    override fun latestUpdatesRequest(page: Int) = GET("$baseUrl/bbs/board.php?bo_table=manga" + if (page > 1) "&page=$page" else "")
    override fun latestUpdatesNextPageSelector() = "ul.pagination > li:not(.disabled)"

    // We are able to get the image URL directly from the page list
    override fun imageUrlParse(document: Document) = throw UnsupportedOperationException("This method should not be called!")

    private fun urlFinder(style: String): String {
        // val regex = Regex("(https?:)?//[-a-zA-Z0-9@:%._\\\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\\\+.~#?&/=]*)")
        // return regex.find(style)!!.value
        return style.substringAfter("background-image:url(").substringBefore(")")
    }

    private fun Elements.trimText(fallback: String = ""): String {
        return this.text()?.trim()?.takeUnless { it.isBlank() } ?: fallback
    }

    private val preferences: SharedPreferences by lazy {
        Injekt.get<Application>().getSharedPreferences("source_$id", 0x0000)
    }

    override fun setupPreferenceScreen(screen: androidx.preference.PreferenceScreen) {
        val baseUrlPref = androidx.preference.EditTextPreference(screen.context).apply {
            key = BASE_URL_PREF_TITLE
            title = BASE_URL_PREF_TITLE
            summary = BASE_URL_PREF_SUMMARY
            this.setDefaultValue(defaultBaseUrl)
            dialogTitle = BASE_URL_PREF_TITLE
            dialogMessage = "Default: $defaultBaseUrl"

            setOnPreferenceChangeListener { _, newValue ->
                try {
                    val res = preferences.edit().putString(BASE_URL_PREF, newValue as String).commit()
                    Toast.makeText(screen.context, RESTART_TACHIYOMI, Toast.LENGTH_LONG).show()
                    res
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        val autoFetchUrlPref = androidx.preference.CheckBoxPreference(screen.context).apply {
            key = AUTOFETCH_URL_PREF_TITLE
            title = AUTOFETCH_URL_PREF_TITLE
            summary = AUTOFETCH_URL_PREF_SUMMARY
            this.setEnabled(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

            setOnPreferenceChangeListener { _, newValue ->
                try {
                    val res = preferences.edit().putBoolean(AUTOFETCH_URL_PREF, newValue as Boolean).commit()
                    Toast.makeText(screen.context, RESTART_TACHIYOMI, Toast.LENGTH_LONG).show()
                    res
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        val latestExperimentPref = androidx.preference.CheckBoxPreference(screen.context).apply {
            key = EXPERIMENTAL_LATEST_PREF_TITLE
            title = EXPERIMENTAL_LATEST_PREF_TITLE
            summary = EXPERIMENTAL_LATEST_PREF_SUMMARY

            setOnPreferenceChangeListener { _, newValue ->
                try {
                    val res = preferences.edit().putBoolean(EXPERIMENTAL_LATEST_PREF, newValue as Boolean).commit()
                    Toast.makeText(screen.context, RESTART_TACHIYOMI, Toast.LENGTH_LONG).show()
                    res
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        screen.addPreference(baseUrlPref)
        screen.addPreference(autoFetchUrlPref)
        screen.addPreference(latestExperimentPref)
    }

    override fun setupPreferenceScreen(screen: PreferenceScreen) {
        val baseUrlPref = EditTextPreference(screen.context).apply {
            key = BASE_URL_PREF_TITLE
            title = BASE_URL_PREF_TITLE
            summary = BASE_URL_PREF_SUMMARY
            this.setDefaultValue(defaultBaseUrl)
            dialogTitle = BASE_URL_PREF_TITLE
            dialogMessage = "Default: $defaultBaseUrl"

            setOnPreferenceChangeListener { _, newValue ->
                try {
                    val res = preferences.edit().putString(BASE_URL_PREF, newValue as String).commit()
                    Toast.makeText(screen.context, RESTART_TACHIYOMI, Toast.LENGTH_LONG).show()
                    res
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        val autoFetchUrlPref = CheckBoxPreference(screen.context).apply {
            key = AUTOFETCH_URL_PREF_TITLE
            title = AUTOFETCH_URL_PREF_TITLE
            summary = AUTOFETCH_URL_PREF_SUMMARY
            this.setEnabled(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

            setOnPreferenceChangeListener { _, newValue ->
                try {
                    val res = preferences.edit().putBoolean(AUTOFETCH_URL_PREF, newValue as Boolean).commit()
                    Toast.makeText(screen.context, RESTART_TACHIYOMI, Toast.LENGTH_LONG).show()
                    res
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        val latestExperimentPref = CheckBoxPreference(screen.context).apply {
            key = EXPERIMENTAL_LATEST_PREF_TITLE
            title = EXPERIMENTAL_LATEST_PREF_TITLE
            summary = EXPERIMENTAL_LATEST_PREF_SUMMARY

            setOnPreferenceChangeListener { _, newValue ->
                try {
                    val res = preferences.edit().putBoolean(EXPERIMENTAL_LATEST_PREF, newValue as Boolean).commit()
                    Toast.makeText(screen.context, RESTART_TACHIYOMI, Toast.LENGTH_LONG).show()
                    res
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        screen.addPreference(baseUrlPref)
        screen.addPreference(autoFetchUrlPref)
        screen.addPreference(latestExperimentPref)
    }

    private fun getCurrentBaseUrl(): String {
        val prefBaseUrl = getPrefBaseUrl()
        if (!preferences.getBoolean(AUTOFETCH_URL_PREF, false)) {
            return prefBaseUrl
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                @TargetApi(Build.VERSION_CODES.O)
                class CallbackFuture : CompletableFuture<Response?>(), Callback {
                    override fun onResponse(call: Call?, response: Response?) {
                        super.complete(response)
                    }

                    override fun onFailure(call: Call?, e: IOException?) {
                        super.completeExceptionally(e)
                    }
                }

                val request: Request = Request.Builder().get()
                    // .url("https://mnmnmnmnm.xyz/")
                    .url("http://52.74.159.59")
                    .build()

                val future = CallbackFuture()
                network.client
                    // .newBuilder()
                    // .addInterceptor(DDOSGuardInterceptor())
                    // .build()!!
                    .newCall(request).enqueue(future)

                val response = future.get()!!
                return "https://${response.request().url().host()}"
                // val code = response.body().toString().substringBetween("manamoa", ".net")
                // return "https://manamoa$code.net"
            } catch (e: Exception) {
                e.printStackTrace()
                return prefBaseUrl
            }
        } else {
            return prefBaseUrl
        }
    }

    private fun getPrefBaseUrl(): String = preferences.getString(BASE_URL_PREF, defaultBaseUrl)!!
    private fun getExperimentLatest(): Boolean = preferences.getBoolean(EXPERIMENTAL_LATEST_PREF, false)

    override fun getFilterList() = getFilters()

    companion object {
        // Setting: Override BaseUrl
        private const val BASE_URL_PREF_TITLE = "Override BaseUrl"
        private const val BASE_URL_PREF = "overrideBaseUrl_v${BuildConfig.VERSION_NAME}"
        private const val BASE_URL_PREF_SUMMARY = "For temporary uses. Update extension will erase this setting."
        // Setting: Fetch Domain
        private const val AUTOFETCH_URL_PREF_TITLE = "Automatically fetch new domain"
        private const val AUTOFETCH_URL_PREF = "autoFetchNewUrl"
        private const val AUTOFETCH_URL_PREF_SUMMARY =
            "Experimental, May cause Tachiyomi *very* unstable.\n" +
                "Requires Android Oreo or newer."

        // Setting: Experimental Latest Fetcher
        private const val EXPERIMENTAL_LATEST_PREF_TITLE = "Enable Latest (Experimental)"
        private const val EXPERIMENTAL_LATEST_PREF = "fetchLatestExperiment"
        private const val EXPERIMENTAL_LATEST_PREF_SUMMARY = "Fetch Latest Manga using Latest Chapters. May has duplicates or invalid name."

        private const val RESTART_TACHIYOMI = "Restart Tachiyomi to apply new setting."

        // Image Decoder
        internal const val V1_CX = 5
        internal const val V1_CY = 5

        // Url Handler
        internal const val MINIMUM_IMAGE_SIZE = 10000

        // Activity Url Handler
        const val PREFIX_ID_SEARCH = "id:"
    }
}
