package eu.kanade.tachiyomi.extension.all.mangadventure

import android.net.Uri
import android.os.Build.VERSION
import eu.kanade.tachiyomi.extension.BuildConfig
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.asObservableSuccess
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import rx.Observable
import java.text.SimpleDateFormat
import java.util.Locale

/** MangAdventure source. */
open class MangAdventure(
        override val name: String,
        override val baseUrl: String,
        val categories: Array<String> = DEFAULT_CATEGORIES,
        override val lang: String = "en",
        override val versionId: Int = 1,
        apiPath: String = "/api") : HttpSource() {

    /** The URL to the site's API. */
    open val apiUrl by lazy { "$baseUrl/$apiPath/v$versionId" }

    override val supportsLatest = true

    /**
     * A user agent representing Tachiyomi.
     * Includes the user's Android version
     * and the current extension version.
     */
    private val userAgent = "Mozilla/5.0 " +
            "(Android ${VERSION.RELEASE}; Mobile) " +
            "Tachiyomi/${BuildConfig.VERSION_NAME}"

    override fun headersBuilder() = Headers.Builder().apply {
        add("User-Agent", userAgent)
        add("Referer", baseUrl)
    }

    override fun latestUpdatesRequest(page: Int) = GET(
        "$apiUrl/releases/", headers
    )

    override fun pageListRequest(chapter: SChapter) = GET(
        "$apiUrl/series/${chapter.url.substringAfter("/reader/")}", headers
    )

    override fun chapterListRequest(manga: SManga) = GET(
        "$apiUrl/series/${Uri.parse(manga.url).lastPathSegment}/", headers
    )

    // Workaround to allow "Open in browser" to use the real URL
    override fun fetchMangaDetails(manga: SManga): Observable<SManga> = client
            .newCall(chapterListRequest(manga))
            .asObservableSuccess().map { res ->
                mangaDetailsParse(res).also { it.initialized = true }
            }

    // Return the real URL for "Open in browser"
    override fun mangaDetailsRequest(manga: SManga) = GET(manga.url, headers)

    override fun searchMangaRequest(page: Int, query: String,
                                    filters: FilterList): Request {
        val uri = Uri.parse("$apiUrl/series/").buildUpon()
        uri.appendQueryParameter("q", query)
        val cat = mutableListOf<String>()
        filters.forEach {
            when (it) {
                is Person -> uri.appendQueryParameter("author", it.state)
                is Status -> uri.appendQueryParameter("status", it.string())
                is CategoryList -> cat.addAll(it.state.mapNotNull { c ->
                    Uri.encode(c.optString())
                })
            }
        }
        return GET("$uri&categories=${cat.joinToString(",")}", headers)
    }

    override fun latestUpdatesParse(response: Response): MangasPage {
        val arr = JSONArray(response.body()!!.string())
        val ret = mutableListOf<SManga>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            ret.add(SManga.create().apply {
                url = obj.getString("url")
                title = obj.getString("title")
                thumbnail_url = obj.getString("cover")
                // A bit of a hack to sort by date
                description = httpDateToTimestamp(
                    obj.getJSONObject("latest_chapter").getString("date")
                ).toString()
            })
        }
        return MangasPage(ret.sortedByDescending { it.description }, false)
    }

    override fun chapterListParse(response: Response): List<SChapter> {
        val res = JSONObject(response.body()!!.string())
        val volumes = res.getJSONObject("volumes")
        val ret = mutableListOf<SChapter>()
        volumes.keys().forEach { vol ->
            val chapters = volumes.getJSONObject(vol)
            chapters.keys().forEach { ch ->
                ret.add(SChapter.create().fromJSON(
                    chapters.getJSONObject(ch).also {
                        it.put("volume", vol)
                        it.put("chapter", ch)
                    }
                ))
            }
        }
        return ret.sortedByDescending { it.name }
    }

    override fun mangaDetailsParse(response: Response) =
            SManga.create().fromJSON(JSONObject(response.body()!!.string()))

    override fun pageListParse(response: Response): List<Page> {
        val obj = JSONObject(response.body()!!.string())
        val url = obj.getString("url")
        val root = obj.getString("pages_root")
        val arr = obj.getJSONArray("pages_list")
        val ret = mutableListOf<Page>()
        for (i in 0 until arr.length()) {
            ret.add(Page(i, "$url${i + 1}", root + arr.getString(i)))
        }
        return ret
    }

    override fun searchMangaParse(response: Response): MangasPage {
        val arr = JSONArray(response.body()!!.string())
        val ret = mutableListOf<SManga>()
        for (i in 0 until arr.length()) {
            ret.add(SManga.create().apply {
                fromJSON(arr.getJSONObject(i))
            })
        }
        return MangasPage(ret.sortedBy { it.title }, false)
    }

    override fun getFilterList() = FilterList(
        Person(), Status(), CategoryList()
    )

    override fun fetchPopularManga(page: Int) =
            fetchSearchManga(page, "", FilterList())

    override fun popularMangaRequest(page: Int) =
            throw UnsupportedOperationException(
                "This method should not be called!"
            )

    override fun popularMangaParse(response: Response) =
            throw UnsupportedOperationException(
                "This method should not be called!"
            )

    override fun imageUrlParse(response: Response) =
            throw UnsupportedOperationException(
                "This method should not be called!"
            )

    companion object {
        /** The possible statuses of a manga. */
        private val STATUSES = arrayOf("Any", "Completed", "Ongoing")

        /** Manga categories from MangAdventure `categories.xml` fixture. */
        internal val DEFAULT_CATEGORIES = arrayOf(
            "4-Koma",
            "Action",
            "Adventure",
            "Comedy",
            "Doujinshi",
            "Drama",
            "Ecchi",
            "Fantasy",
            "Gender Bender",
            "Harem",
            "Hentai",
            "Historical",
            "Horror",
            "Josei",
            "Martial Arts",
            "Mecha",
            "Mystery",
            "Psychological",
            "Romance",
            "School Life",
            "Sci-Fi",
            "Seinen",
            "Shoujo",
            "Shoujo Ai",
            "Shounen",
            "Shounen Ai",
            "Slice of Life",
            "Smut",
            "Sports",
            "Supernatural",
            "Tragedy",
            "Yaoi",
            "Yuri"
        )

        /**
         * The HTTP date format specified in
         * [RFC 1123](https://tools.ietf.org/html/rfc1123#page-55).
         */
        private const val HTTP_DATE = "EEE, dd MMM yyyy HH:mm:ss zzz"

        /**
         * Converts a date in the [HTTP_DATE] format to a Unix timestamp.
         *
         * @param date The date to convert.
         * @return The timestamp of the date.
         */
        fun httpDateToTimestamp(date: String) =
                SimpleDateFormat(HTTP_DATE, Locale.US).parse(date).time
    }

    /**
     * Filter representing the status of a manga.
     *
     * @constructor Creates a [Filter.Select] object with [STATUSES].
     */
    inner class Status : Filter.Select<String>("Status", STATUSES) {
        /** Returns the [state] as a string. */
        fun string() = values[state].toLowerCase()
    }

    /**
     * Filter representing a manga category.
     *
     * @property name The display name of the category.
     * @constructor Creates a [Filter.TriState] object using [name].
     */
    inner class Category(name: String) : Filter.TriState(name) {
        /** Returns the [state] as a string, or null if [isIgnored]. */
        fun optString() = when (state) {
            STATE_INCLUDE -> name.toLowerCase()
            STATE_EXCLUDE -> "-" + name.toLowerCase()
            else -> null
        }
    }

    /**
     * Filter representing the [categories][Category] of a manga.
     *
     * @constructor Creates a [Filter.Group] object with categories.
     */
    inner class CategoryList : Filter.Group<Category>(
        "Categories", categories.map { Category(it) }
    )

    /**
     * Filter representing the name of an author or artist.
     *
     * @constructor Creates a [Filter.Text] object.
     */
    inner class Person : Filter.Text("Author/Artist")
}

