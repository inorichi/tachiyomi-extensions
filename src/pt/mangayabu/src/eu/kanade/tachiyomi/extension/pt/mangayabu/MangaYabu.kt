package eu.kanade.tachiyomi.extension.pt.mangayabu

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.POST
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import rx.Observable

class MangaYabu : ParsedHttpSource() {

    // Hardcode the id because the language wasn't specific.
    override val id: Long = 7152688036023311164

    override val name = "MangaYabu!"

    override val baseUrl = "https://mangayabu.com"

    override val lang = "pt-BR"

    override val supportsLatest = true

    override fun headersBuilder(): Headers.Builder = Headers.Builder()
        .add("User-Agent", USER_AGENT)
        .add("Origin", baseUrl)
        .add("Referer", baseUrl)

    override fun fetchPopularManga(page: Int): Observable<MangasPage> {
        return super.fetchPopularManga(page)
            .map {
                MangasPage(it.mangas.distinctBy { m -> m.url }, it.hasNextPage)
            }
    }

    override fun popularMangaRequest(page: Int): Request = GET(baseUrl, headers)

    override fun popularMangaSelector(): String = "div.loop-content div.owl-carousel div.video a.clip-link"

    override fun popularMangaFromElement(element: Element): SManga = SManga.create().apply {
        title = element.attr("title").substringBefore(" –")
        thumbnail_url = element.select("span.clip img")!!.attr("src")
        url = mapChapterToMangaUrl(element.attr("href"))
    }

    override fun popularMangaNextPageSelector(): String? = null

    override fun fetchLatestUpdates(page: Int): Observable<MangasPage> {
        return super.fetchLatestUpdates(page)
            .map {
                MangasPage(it.mangas.distinctBy { m -> m.url }, it.hasNextPage)
            }
    }

    override fun latestUpdatesRequest(page: Int): Request {
        val pagePath = if (page == 1) "" else "/page/$page"
        return GET("$baseUrl$pagePath", headers)
    }

    override fun latestUpdatesSelector() = "div.loop-content.phpvibe-video-list.miau div.video a.clip-link"

    override fun latestUpdatesFromElement(element: Element): SManga = SManga.create().apply {
        val image = element.select("span.clip img")!!

        title = image.attr("alt").substringBefore(" –")
        thumbnail_url = image.attr("src")
        url = mapChapterToMangaUrl(element.attr("href"))
    }

    override fun latestUpdatesNextPageSelector() = "div#pagination a div.item.icon i:contains(arrow_forward_ios)"

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val newHeaders = headers.newBuilder()
            .set("X-Requested-With", "XMLHttpRequest")
            .build()

        val form = FormBody.Builder()
            .add("action", "data_fetch")
            .add("search_keyword", query)
            .build()

        return POST("$baseUrl/wp-admin/admin-ajax.php", newHeaders, form)
    }

    override fun searchMangaSelector() = "ul li.gsuggested a"

    override fun searchMangaFromElement(element: Element): SManga = SManga.create().apply {
        title = element.select("div.contento span.search-name")!!.text()
        thumbnail_url = "$baseUrl/${element.select("img.search-thumb")!!.attr("src")}"
        setUrlWithoutDomain(element.attr("href"))
    }

    override fun searchMangaNextPageSelector(): String? = null

    override fun mangaDetailsParse(document: Document): SManga {
        val infoElement = document.select("div#channel-content div.row").first()!!
        val statusStr = infoElement.select("div.left20 p:contains(Status)")!!.text()
            .substringAfter("Status: ")

        return SManga.create().apply {
            title = infoElement.select("div.left20 h1")!!.text()
            status = when (statusStr) {
                "Em lançamento" -> SManga.ONGOING
                "Completo" -> SManga.COMPLETED
                else -> SManga.UNKNOWN
            }
            description = infoElement.select("div.left20 p[style] + p").first()!!.text()
            thumbnail_url = infoElement.select("div.mleft20 img.channel-img")!!.attr("src")
        }
    }

    override fun chapterListSelector() = "div.loop-content div.chap-holder a.chapter-link"

    override fun chapterFromElement(element: Element): SChapter = SChapter.create().apply {
        name = "Capítulo " + element.select("small")!!.text()
        chapter_number = element.select("small")!!.text().toFloatOrNull() ?: 0f
        setUrlWithoutDomain(element.attr("href"))
    }

    override fun pageListParse(document: Document): List<Page> {
        return document.select("img.img-responsive.img-manga")
            .mapIndexed { i, element -> Page(i, "", element.absUrl("src")) }
    }

    override fun imageUrlParse(document: Document) = ""

    /**
     * Some mangas doesn't use the same slug from the chapter url, and
     * since the site doesn't have a proper popular list yet, we have
     * to deal with some exceptions and map them to the correct
     * slug manually.
     *
     * It's a bad solution, but it's a working one for now.
     */
    private fun mapChapterToMangaUrl(chapterUrl: String): String {
        val chapterSlug = chapterUrl
            .substringBefore("-capitulo")
            .substringAfter("ler/")

        return "/manga/" + (SLUG_EXCEPTIONS[chapterSlug] ?: chapterSlug)
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36"

        private val SLUG_EXCEPTIONS = mapOf(
            "the-promised-neverland-yakusoku-no-neverland" to "yakusoku-no-neverland-the-promised-neverland"
        )
    }
}
