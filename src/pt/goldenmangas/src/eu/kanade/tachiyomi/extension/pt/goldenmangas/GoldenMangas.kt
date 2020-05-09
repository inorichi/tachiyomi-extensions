package eu.kanade.tachiyomi.extension.pt.goldenmangas

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class GoldenMangas : ParsedHttpSource() {

    // Hardcode the id because the language wasn't specific.
    override val id: Long = 6858719406079923084

    override val name = "Golden Mangás"

    override val baseUrl = "https://goldenmangas.online"

    override val lang = "pt-BR"

    override val supportsLatest = true

    override val client: OkHttpClient = network.client.newBuilder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .build()

    override fun headersBuilder(): Headers.Builder = Headers.Builder()
        .add("User-Agent", USER_AGENT)
        .add("Origin", baseUrl)
        .add("Referer", baseUrl)

    override fun popularMangaRequest(page: Int): Request = GET(baseUrl, headers)

    override fun popularMangaSelector(): String = "div#maisLidos div.itemmanga"

    override fun popularMangaFromElement(element: Element): SManga = SManga.create().apply {
        title = element.select("h3").first().text().withoutLanguage()
        thumbnail_url = element.select("img").first()?.attr("abs:src")
        url = element.attr("href")
    }

    override fun popularMangaNextPageSelector(): String? = null

    override fun latestUpdatesRequest(page: Int): Request {
        val path = if (page > 1) "/index.php?pagina=$page" else ""
        return GET("$baseUrl$path", headers)
    }

    override fun latestUpdatesSelector() = "div.col-sm-12.atualizacao > div.row"

    override fun latestUpdatesFromElement(element: Element): SManga = SManga.create().apply {
        val infoElement = element.select("div.col-sm-10.col-xs-8 h3").first()
        val thumb = element.select("a:first-child div img").first().attr("abs:src")

        title = infoElement.text().withoutLanguage()
        thumbnail_url = thumb.replace("w=80&h=120", "w=380&h=600")
        url = element.select("a:first-child").attr("href")
    }

    override fun latestUpdatesNextPageSelector() = "ul.pagination li:last-child a"

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val newHeaders = headers.newBuilder()
            .set("Referer", "$baseUrl/mangas")
            .build()

        val url = HttpUrl.parse("$baseUrl/mangabr")!!.newBuilder()
            .addQueryParameter("busca", query)

        return GET(url.toString(), newHeaders)
    }

    override fun searchMangaSelector() = "div.mangas.col-lg-2 a"

    override fun searchMangaFromElement(element: Element): SManga = SManga.create().apply {
        title = element.select("h3").first().text().withoutLanguage()
        thumbnail_url = element.select("img").first().attr("abs:src")
        url = element.attr("href")
    }

    override fun searchMangaNextPageSelector() = "ul.pagination li:last-child a"

    override fun mangaDetailsParse(document: Document): SManga = SManga.create().apply {
        val infoElement = document.select("div.row > div.col-sm-8 > div.row").first()
        val firstColumn = infoElement.select("div.col-sm-4.text-right > img").first()
        val secondColumn = infoElement.select("div.col-sm-8").first()

        title = secondColumn.select("h2:eq(0)").text().withoutLanguage()
        author = secondColumn.select("h5:eq(3)")!!.text().withoutLabel()
        artist = secondColumn.select("h5:eq(4)")!!.text().withoutLabel()
        genre = secondColumn.select("h5:eq(2) a")
            .filter { it.text().isNotEmpty() }
            .joinToString { it.text() }
        status = parseStatus(secondColumn.select("h5:eq(5) a").text().orEmpty())
        description = document.select("#manga_capitulo_descricao").text()
        thumbnail_url = firstColumn.attr("abs:src")
    }

    private fun parseStatus(status: String) = when {
        status.contains("Ativo") -> SManga.ONGOING
        status.contains("Completo") -> SManga.COMPLETED
        else -> SManga.UNKNOWN
    }

    override fun chapterListSelector() = "ul#capitulos li.row"

    override fun chapterFromElement(element: Element): SChapter = SChapter.create().apply {
        val firstColumn = element.select("a > div.col-sm-5")
        val secondColumn = element.select("div.col-sm-5.text-right a[href^='http']")

        name = firstColumn.select("div.col-sm-5").first().text()
            .substringBefore("(").trim()
        scanlator = secondColumn?.joinToString { it.text() }
        date_upload = DATE_FORMATTER.tryParseTime(firstColumn.select("div.col-sm-5 span[style]").first().text())
        url = element.select("a").first().attr("href")
    }

    override fun pageListParse(document: Document): List<Page> {
        val chapterUrl = document.location()
        val chapterImages = document.select("div.col-sm-12[id^='capitulos_images']").first()

        return chapterImages.select("img[pag]")
            .mapIndexed { i, element -> Page(i, chapterUrl, element.attr("abs:src")) }
    }

    override fun imageUrlParse(document: Document) = ""

    override fun imageRequest(page: Page): Request {
        val newHeaders = headersBuilder()
            .set("Referer", page.url)
            .build()

        return GET(page.imageUrl!!, newHeaders)
    }

    private fun String.withoutLanguage(): String = replace(FLAG_REGEX, "").trim()

    private fun String.withoutLabel(): String = substringAfter(":").trim()

    private fun SimpleDateFormat.tryParseTime(date: String): Long {
        return try {
            parse(date).time
        } catch (e: ParseException) {
            0L
        }
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36"

        private val FLAG_REGEX = "\\((Pt[-/]br|Scan)\\)".toRegex(RegexOption.IGNORE_CASE)

        private val DATE_FORMATTER by lazy { SimpleDateFormat("(dd/MM/yyyy)", Locale.ENGLISH) }
    }
}
