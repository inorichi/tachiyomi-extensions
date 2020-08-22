package eu.kanade.tachiyomi.extension.zh.onemanhua

import android.annotation.SuppressLint
import android.util.Base64
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import okhttp3.HttpUrl
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Onemanhua : ParsedHttpSource() {
    override val id = 8252565807829914103 // name used to be "One漫画"
    override val lang = "zh"
    override val supportsLatest = true
    override val name = "OH漫画 (One漫画)"
    override val baseUrl = "https://www.ohmanhua.com/"

    private var decryptKey = "fw12558899ertyui"
    private var imageServerUrl = "https://img.ohmanhua.com/comic/"

    // Common
    private var commonSelector = "li.fed-list-item"
    private var commonNextPageSelector = "a:contains(下页):not(.fed-btns-disad)"
    private fun commonMangaFromElement(element: Element): SManga {
        val picElement = element.select("a.fed-list-pics").first()
        val manga = SManga.create().apply {
            title = element.select("a.fed-list-title").first().text()
            thumbnail_url = picElement.attr("data-original")
        }

        manga.setUrlWithoutDomain(picElement.attr("href"))

        return manga
    }

    // Popular Manga
    override fun popularMangaRequest(page: Int) = GET("$baseUrl/show?orderBy=dailyCount&page=$page", headers)
    override fun popularMangaNextPageSelector() = commonNextPageSelector
    override fun popularMangaSelector() = commonSelector
    override fun popularMangaFromElement(element: Element) = commonMangaFromElement(element)

    // Latest Updates
    override fun latestUpdatesRequest(page: Int) = GET("$baseUrl/show?orderBy=update&page=$page", headers)
    override fun latestUpdatesNextPageSelector() = commonNextPageSelector
    override fun latestUpdatesSelector() = commonSelector
    override fun latestUpdatesFromElement(element: Element) = commonMangaFromElement(element)

    // Filter
    private class StatusFilter : Filter.TriState("已完结")
    private class SortFilter : Filter.Select<String>("排序", arrayOf("更新日", "收录日", "日点击", "月点击"), 2)
    override fun getFilterList() = FilterList(
        SortFilter(),
        StatusFilter()
    )

    // Search
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return if (query.isNotBlank()) {
            GET("$baseUrl/search?searchString=$query&page=$page", headers)
        } else {
            val url = HttpUrl.parse("$baseUrl/show")!!.newBuilder()
            url.addQueryParameter("page", page.toString())

            filters.forEach { filter ->
                when (filter) {
                    is StatusFilter -> {
                        if (!filter.isIgnored()) {
                            url.addQueryParameter("status", arrayOf("0", "2", "1")[filter.state])
                        }
                    }
                    is SortFilter -> {
                        url.addQueryParameter("orderBy", arrayOf("update", "create", "dailyCount", "weeklyCount", "monthlyCount")[filter.state])
                    }
                }
            }
            GET(url.toString(), headers)
        }
    }
    override fun searchMangaNextPageSelector() = commonNextPageSelector
    override fun searchMangaSelector() = "dl.fed-deta-info, $commonSelector"
    override fun searchMangaFromElement(element: Element): SManga {
        if (element.tagName() == "li") {
            return commonMangaFromElement(element)
        }

        val picElement = element.select("a.fed-list-pics").first()
        val manga = SManga.create().apply {
            title = element.select("h1.fed-part-eone a").first().text()
            thumbnail_url = picElement.attr("data-original")
        }

        manga.setUrlWithoutDomain(picElement.attr("href"))

        return manga
    }

    override fun mangaDetailsParse(document: Document): SManga {
        val picElement = document.select("a.fed-list-pics").first()
        val detailElements = document.select("ul.fed-part-rows li.fed-col-xs12")
        return SManga.create().apply {
            title = document.select("h1.fed-part-eone").first().text().trim()
            thumbnail_url = picElement.attr("data-original")
            status = when (detailElements[0].select("a").first().text()) {
                "连载中" -> SManga.ONGOING
                "已完结" -> SManga.COMPLETED
                else -> SManga.UNKNOWN
            }
            author = detailElements[1].select("a").first().text()

            genre = detailElements[4].select("a").joinToString { it.text() }
            description = detailElements[5].select(".fed-part-esan").first().text().trim()
        }
    }

    override fun chapterListSelector(): String = "div:not(.fed-hidden) > div.all_data_list > ul.fed-part-rows a"

    override fun chapterFromElement(element: Element): SChapter {
        val chapter = SChapter.create().apply {
            name = element.attr("title")
        }
        chapter.setUrlWithoutDomain(element.attr("href"))
        return chapter
    }

    override fun imageUrlParse(document: Document) = ""

    override fun pageListParse(document: Document): List<Page> {
        // 1. get C_DATA from HTML
        val encodedData = getEncodedMangaData(document)
        // 2. decode C_DATA by Base64
        val decodedData = String(Base64.decode(encodedData, Base64.NO_WRAP))
        // 3. decrypt C_DATA
        val decryptedData = decryptAES(decodedData, decryptKey)

        val imgRelativePath = getImgRelativePath(decryptedData)
        val startImg = getStartImg(decryptedData)
        val totalPages = getTotalPages(decryptedData)

        return mutableListOf<Page>().apply {
            for (i in startImg..totalPages) {
                add(Page(i, "", "${imageServerUrl}${imgRelativePath}${"%04d".format(i)}.jpg"))
            }
        }
    }

    private fun getEncodedMangaData(document: Document): String? {
        val scriptElements = document.getElementsByTag("script")
        val pattern = Pattern.compile("C_DATA=\'(.+?)\'")
        for (element in scriptElements) {
            if (element.data().contains("C_DATA")) {
                val matcher = pattern.matcher(element.data())
                if (matcher.find()) {
                    return matcher.group(1)
                }
            }
        }

        throw Error("Unable to match for C_DATA")
    }

    @SuppressLint("GetInstance")
    private fun decryptAES(value: String, key: String): String {
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")

        return try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey)

            val code = Base64.decode(value, Base64.NO_WRAP)

            String(cipher.doFinal(code))
        } catch (_: Exception) {
            throw Exception("Decryption failed")
        }
    }

    private fun getImgRelativePath(mangaData: String): String {
        val pattern = Pattern.compile("imgpath:\"(.+?)\"")
        val matcher = pattern.matcher(mangaData)
        if (matcher.find()) {
            return matcher.group(1) ?: throw Exception("imgpath not found")
        }

        throw Error("Unable to match for imgPath")
    }

    private fun getTotalPages(mangaData: String): Int {
        val pattern = Pattern.compile("totalimg:([0-9]+?),")
        val matcher = pattern.matcher(mangaData)
        if (matcher.find()) {
            return matcher.group(1)?.let { Integer.parseInt(it) } ?: throw Exception("totalimg not found")
        }

        throw Error("Unable to match for totalimg")
    }

    private fun getStartImg(mangaData: String): Int {
        val pattern = Pattern.compile("startimg:([0-9]+?),")
        val matcher = pattern.matcher(mangaData)
        if (matcher.find()) {
            return matcher.group(1)?.let { Integer.parseInt(it) } ?: throw Exception("startimg not found")
        }

        throw Error("Unable to match for startimg")
    }
}
