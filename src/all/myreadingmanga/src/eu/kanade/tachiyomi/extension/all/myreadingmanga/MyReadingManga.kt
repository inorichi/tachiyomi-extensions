package eu.kanade.tachiyomi.extension.all.myreadingmanga

import android.net.Uri
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.util.asJsoup
import okhttp3.*
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

open class MyReadingManga(override val lang: String) : ParsedHttpSource() {

    override val name = "MyReadingManga"

    override val baseUrl = "https://myreadingmanga.info"

    override val client: OkHttpClient = network.cloudflareClient.newBuilder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .build()!!

    override val supportsLatest = false

    override fun popularMangaSelector() = "article"

    override fun latestUpdatesSelector() = ""

    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl/page/$page/", headers)
    }

    override fun latestUpdatesRequest(page: Int) = popularMangaRequest(page)

    override fun popularMangaParse(response: Response): MangasPage {
        val document = response.asJsoup()

        val mangas = mutableListOf<SManga>()
        val list  = document.select(popularMangaSelector()).filter { element ->
            val select = element.select("a[rel=bookmark]")
            select.text().contains("[$lang", true)
        }
        for (element in list) {
            mangas.add(popularMangaFromElement(element))

        }

        val hasNextPage = popularMangaNextPageSelector().let { selector ->
            document.select(selector).first()
        } != null

        return MangasPage(mangas, hasNextPage)
    }

    override fun popularMangaFromElement(element: Element) = buildManga(element.select("a[rel]").first(), element.select("a.entry-image-link img").first())

    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)

    override fun popularMangaNextPageSelector() = "li.pagination-next"

    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val uri = Uri.parse("$baseUrl/search/").buildUpon()
        uri.appendQueryParameter("search", query)
        return GET(uri.toString())
    }


    override fun searchMangaParse(response: Response): MangasPage {
        val document = response.asJsoup()

        val elements = document.select(searchMangaSelector())
        var mangas = mutableListOf<SManga>()
        for (element in elements) {
            if (element.text().contains("[$lang", true)) {
                mangas.add(searchMangaFromElement(element))
            }
        }

        return MangasPage(mangas, false)
    }

    override fun searchMangaSelector() = "div.results-by-facets div[id*=res]"

    override fun searchMangaFromElement(element: Element) = buildManga(element.select("a").first(), element.select("img").first())

    private fun buildManga(titleElement: Element, thumbnailElement: Element): SManga {
        val manga = SManga.create()
        manga.setUrlWithoutDomain(titleElement.attr("href"))
        manga.title = cleanTitle(titleElement.text())
        manga.thumbnail_url = getThumbnail(getImage(thumbnailElement))
        return manga
    }

    private fun getImage(element: Element): String {
        var url =
                when {
                    element.attr("data-src").endsWith(".jpg") || element.attr("data-src").endsWith(".png") || element.attr("data-src").endsWith(".jpeg") -> element.attr("data-src")
                    element.attr("src").endsWith(".jpg") || element.attr("src").endsWith(".png") || element.attr("src").endsWith(".jpeg") -> element.attr("src")
                    else -> element.attr("data-lazy-src")
                }
        if (url.startsWith("//")) {
            url = "http:$url"
        }
        return url
    }

    //removes resizing
    private fun getThumbnail(thumbnailUrl: String) = thumbnailUrl.substringBeforeLast("-") + "." + thumbnailUrl.substringAfterLast(".")

    //cleans up the name removing author and language from the title
    private fun cleanTitle(title: String) = title.substringBeforeLast("[").substringAfterLast("]").substringBeforeLast("(")


    override fun mangaDetailsParse(document: Document): SManga {
        val manga = SManga.create()
        manga.author = document.select(".entry-content p")?.find { it ->
            it.text().contains("artist", true) || it.text().contains("author", true)
        }?.text()?.substringAfter(":")

        val glist = document.select(".entry-header p a[href*=genre]").map { it -> it.text() }
        manga.genre = glist.joinToString(", ")
        manga.description = document.select("h1").text() + "\n" + (document.select(".entry-content blockquote")?.first()?.text() ?: "")
        manga.status = SManga.UNKNOWN
        return manga
    }

    override fun chapterListSelector() = ".entry-pagination a"

    override fun chapterListParse(response: Response): List<SChapter> {
        val document = response.asJsoup()
        val chapters = mutableListOf<SChapter>()

        val date = parseDate(document.select(".entry-time").attr("datetime").substringBefore("T"))
        //create first chapter since its on main manga page
        chapters.add(createChapter("1", document.baseUri(), date))
        //see if there are multiple chapters or not
        document.select(chapterListSelector())?.let { it ->
            it.forEach {
                if (!it.text().contains("Next »", true)) {
                    chapters.add(createChapter(it.text(), document.baseUri(), date))
                }
            }
        }
        chapters.reverse()

        return chapters
    }

    private fun parseDate(date: String): Long {
        return SimpleDateFormat("yyyy-MM-dd").parse(date).time
    }

    private fun createChapter(pageNumber: String, mangaUrl: String, date: Long): SChapter {
        val chapter = SChapter.create()
        chapter.setUrlWithoutDomain("$mangaUrl/$pageNumber")
        chapter.name = "Ch. $pageNumber"
        chapter.date_upload = date
        return chapter
    }

    override fun searchMangaNextPageSelector() = throw Exception("Not used")


    override fun chapterFromElement(element: Element) = throw Exception("Not used")

    override fun pageListParse(response: Response): List<Page> {
        val body = response.asJsoup()
        val pages = mutableListOf<Page>()
        val elements = body.select("img[data-lazy-src]:not([width='120']):not([data-original-width='300'])")
        for (i in 0 until elements.size) {
            pages.add(Page(i, "", getImage(elements[i])))
        }
        return pages
    }

    override fun pageListParse(document: Document) = throw Exception("Not used")
    override fun imageUrlRequest(page: Page) = throw Exception("Not used")
    override fun imageUrlParse(document: Document) = throw Exception("Not used")

}
