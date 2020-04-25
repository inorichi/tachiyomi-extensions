package eu.kanade.tachiyomi.extension.en.mangajar

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class MangaJar : ParsedHttpSource() {

    override val name = "MangaJar"

    override val baseUrl = "https://mangajar.com"

    override val lang = "en"

    override val supportsLatest = true

    override val client: OkHttpClient = network.cloudflareClient

    override fun popularMangaSelector() = "article:has(div.post-description)"

    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl/manga?sortBy=popular&page=$page")
    }

    override fun latestUpdatesSelector() = popularMangaSelector()

    override fun latestUpdatesRequest(page: Int): Request {
        return GET("$baseUrl/manga?sortBy=last_chapter_at&page=$page")
    }

    override fun popularMangaFromElement(element: Element) = SManga.create().apply {
        setUrlWithoutDomain(element.select("a").attr("href"))
        title = element.select("p.card-title.js-card-title").text()
        thumbnail_url = element.select("img").attr("src")
    }

    override fun latestUpdatesFromElement(element: Element): SManga = popularMangaFromElement(element)

    override fun popularMangaNextPageSelector() = "[rel=next]"

    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return GET("$baseUrl/search?q=$query&page=$page")
    }

    override fun searchMangaSelector() = popularMangaSelector()

    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)

    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()

    override fun mangaDetailsParse(document: Document) = SManga.create().apply {
        description = document.select("div.manga-description.entry > div").text()
        thumbnail_url = document.select("div.row > div > img").attr("src")
        genre = document.select("div.post-info > span > a[href*=genre]").joinToString { it.text() }
        status = document.select("span:has(b)").get(1).text().let {
            parseStatus(it)
        }
    }

    private fun parseStatus(status: String) = when {
        status.contains("Ongoing") -> SManga.ONGOING
        status.contains("Completed") -> SManga.COMPLETED
        else -> SManga.UNKNOWN
    }

    override fun chapterListRequest(manga: SManga) = GET(baseUrl + manga.url + "/chaptersList")

    override fun chapterListSelector() = "li.list-group-item.chapter-item.d-none"

    override fun chapterFromElement(element: Element) = SChapter.create().apply {
        setUrlWithoutDomain(element.select("a").attr("href"))
        name = element.select("span.chapter-title").text().trim()
        date_upload = parseChapterDate(element.select("span.chapter-date").text().trim()) ?: 0
    }

    // The following date related code is taken directly from Genkan.kt
    companion object {
        val dateFormat by lazy {
            SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        }
    }

    private fun parseChapterDate(string: String): Long? {
        return if ("ago" in string) {
            parseRelativeDate(string) ?: 0
        } else {
            dateFormat.parse(string).time
        }
    }

    private fun parseRelativeDate(date: String): Long? {
        val trimmedDate = date.substringBefore(" ago").removeSuffix("s").split(" ")

        val calendar = Calendar.getInstance()
        when (trimmedDate[1]) {
            "month" -> calendar.apply { add(Calendar.MONTH, -trimmedDate[0].toInt()) }
            "week" -> calendar.apply { add(Calendar.WEEK_OF_MONTH, -trimmedDate[0].toInt()) }
            "day" -> calendar.apply { add(Calendar.DAY_OF_MONTH, -trimmedDate[0].toInt()) }
            "hour" -> calendar.apply { add(Calendar.HOUR_OF_DAY, -trimmedDate[0].toInt()) }
            "minute" -> calendar.apply { add(Calendar.MINUTE, -trimmedDate[0].toInt()) }
            "second" -> calendar.apply { add(Calendar.SECOND, 0) }
        }

        return calendar.timeInMillis
    }

    override fun pageListParse(document: Document): List<Page> {
        return document.select("img[data-page]").mapIndexed { i, element ->
            Page(i, "", element.attr("src"))
        }
    }

    override fun imageUrlParse(document: Document): String = throw UnsupportedOperationException("Not Used")

    override fun getFilterList() = FilterList()
}
