package eu.kanade.tachiyomi.extension.es.tumangaonline

import android.app.Application
import android.content.SharedPreferences
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceScreen
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.ConfigurableSource
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.util.asJsoup
import java.text.SimpleDateFormat
import java.util.Locale
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class TuMangaOnline : ConfigurableSource, ParsedHttpSource() {

    // Info

    override val name = "TuMangaOnline"
    override val baseUrl = "https://lectortmo.com"
    override val lang = "es"
    override val supportsLatest = true

    // Client

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36"
    override fun headersBuilder(): Headers.Builder {
        return Headers.Builder()
            .add("User-Agent", userAgent)
            .add("Referer", "$baseUrl/")
            .add("Cache-mode", "no-cache")
    }

    private fun getBuilder(url: String, headers: Headers, formBody: FormBody?, method: String): String {
        val req = Request.Builder()
            .headers(headers)
            .url(url)
            .method(method, formBody)
            .build()

        return client.newCall(req)
            .execute()
            .request()
            .url()
            .toString()
    }

    // Popular

    override fun popularMangaRequest(page: Int) = GET("$baseUrl/library?order_item=likes_count&order_dir=desc&filter_by=title&_page=1&page=$page", headers)
    override fun popularMangaNextPageSelector() = "a.page-link"
    override fun popularMangaSelector() = "div.element"
    override fun popularMangaFromElement(element: Element) = SManga.create().apply {
        element.select("div.element > a").let {
            setUrlWithoutDomain(it.attr("href").substringAfter(" "))
            title = it.select("h4.text-truncate").text()
            thumbnail_url = it.select("style").toString().substringAfter("('").substringBeforeLast("')")
        }
    }

    // Latest

    override fun latestUpdatesRequest(page: Int) = GET("$baseUrl/library?order_item=creation&order_dir=desc&filter_by=title&_page=1&page=$page", headers)
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()
    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)

    // Search

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val url = HttpUrl.parse("$baseUrl/library")!!.newBuilder()

        url.addQueryParameter("title", query)
        url.addQueryParameter("page", page.toString())
        url.addQueryParameter("_page", "1") // Extra Query to Prevent Scrapping aka without it = 403

        filters.forEach { filter ->
            when (filter) {
                is Types -> {
                    url.addQueryParameter("type", filter.toUriPart())
                }
                is Demography -> {
                    url.addQueryParameter("demography", filter.toUriPart())
                }
                is FilterBy -> {
                    url.addQueryParameter("filter_by", filter.toUriPart())
                }
                is SortBy -> {
                    if (filter.state != null) {
                        url.addQueryParameter("order_item", SORTABLES[filter.state!!.index].second)
                        url.addQueryParameter(
                            "order_dir",
                            if (filter.state!!.ascending) { "asc" } else { "desc" }
                        )
                    }
                }
                is WebcomicFilter -> {
                    url.addQueryParameter("webcomic", when (filter.state) {
                        Filter.TriState.STATE_INCLUDE -> "true"
                        Filter.TriState.STATE_EXCLUDE -> "false"
                        else -> ""
                    })
                }
                is FourKomaFilter -> {
                    url.addQueryParameter("yonkoma", when (filter.state) {
                        Filter.TriState.STATE_INCLUDE -> "true"
                        Filter.TriState.STATE_EXCLUDE -> "false"
                        else -> ""
                    })
                }
                is AmateurFilter -> {
                    url.addQueryParameter("amateur", when (filter.state) {
                        Filter.TriState.STATE_INCLUDE -> "true"
                        Filter.TriState.STATE_EXCLUDE -> "false"
                        else -> ""
                    })
                }
                is EroticFilter -> {
                    url.addQueryParameter("erotic", when (filter.state) {
                        Filter.TriState.STATE_INCLUDE -> "true"
                        Filter.TriState.STATE_EXCLUDE -> "false"
                        else -> ""
                    })
                }
                is GenreList -> {
                    filter.state
                        .filter { genre -> genre.state }
                        .forEach { genre -> url.addQueryParameter("genders[]", genre.id) }
                }
            }
        }

        return GET(url.build().toString(), headers)
    }

    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()
    override fun searchMangaFromElement(element: Element): SManga = popularMangaFromElement(element)

    // Details

    override fun mangaDetailsParse(document: Document) = SManga.create().apply {
        document.select("h5.card-title").let {
            author = it?.first()?.attr("title")?.substringAfter(", ")
            artist = it?.last()?.attr("title")?.substringAfter(", ")
        }

        genre = document.select("a.py-2").joinToString(", ") {
            it.text()
        }

        description = document.select("p.element-description")?.text()
        status = parseStatus(document.select("span.book-status")?.text().orEmpty())
        thumbnail_url = document.select(".book-thumbnail").attr("src")
    }

    private fun parseStatus(status: String) = when {
        status.contains("Publicándose") -> SManga.ONGOING
        status.contains("Finalizado") -> SManga.COMPLETED
        else -> SManga.UNKNOWN
    }

    // Chapters

    override fun chapterListParse(response: Response): List<SChapter> {
        val document = response.asJsoup()
        val chapterUrl = response.request().url().toString()

        // One-shot
        if (document.select("div.chapters").isEmpty()) {
            return document.select(oneShotChapterListSelector()).map { oneShotChapterFromElement(it, chapterUrl) }
        }

        // Regular list of chapters
        val chapters = mutableListOf<SChapter>()
        document.select(regularChapterListSelector()).forEach { chapelement ->
            val chapternumber = chapelement.select("a.btn-collapse").text()
                .substringBefore(":")
                .substringAfter("Capítulo")
                .trim()
                .toFloat()
            val chaptername = chapelement.select("div.col-10.text-truncate").text()
            val scanelement = chapelement.select("ul.chapter-list > li")
            val dupselect = getduppref()!!
            if (dupselect == "one") {
                scanelement.first { chapters.add(regularChapterFromElement(it, chaptername, chapternumber, chapterUrl)) }
            } else {
                scanelement.forEach { chapters.add(regularChapterFromElement(it, chaptername, chapternumber, chapterUrl)) }
            }
        }
        return chapters
    }

    override fun chapterListSelector() = throw UnsupportedOperationException("Not used")

    override fun chapterFromElement(element: Element) = throw UnsupportedOperationException("Not used")

    private fun oneShotChapterListSelector() = "div.chapter-list-element > ul.list-group li.list-group-item"

    private fun oneShotChapterFromElement(element: Element, chapterUrl: String) = SChapter.create().apply {
        url = "$chapterUrl#${element.select("div.row > .text-right > form").attr("id")}"
        name = "One Shot"
        scanlator = element.select("div.col-md-6.text-truncate")?.text()
        date_upload = element.select("span.badge.badge-primary.p-2").first()?.text()?.let { parseChapterDate(it) }
            ?: 0
    }

    private fun regularChapterListSelector() = "div.chapters > ul.list-group li.p-0.list-group-item"

    private fun regularChapterFromElement(element: Element, chName: String, number: Float, chapterUrl: String) = SChapter.create().apply {
        url = "$chapterUrl#${element.select("div.row > .text-right > form").attr("id")}"
        name = chName
        chapter_number = number
        scanlator = element.select("div.col-md-6.text-truncate")?.text()
        date_upload = element.select("span.badge.badge-primary.p-2").first()?.text()?.let { parseChapterDate(it) }
            ?: 0
    }

    private fun parseChapterDate(date: String): Long = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)?.time
        ?: 0

    // Pages

    override fun pageListRequest(chapter: SChapter): Request {
        val (chapterURL, chapterID) = chapter.url.split("#")
        val response = client.newCall(GET(chapterURL, headers)).execute() // Get chapter page for current token
        if (!response.isSuccessful) throw Exception("TMO HTTP Error ${response.code()}")
        val document = response.asJsoup()
        val script = document.select("script:containsData(submitChapterForm)").html()
        val tmotk = script.substringAfter("action+\"").substringBefore("\"}")
        val geturl = document.select("form#$chapterID").attr("action") + tmotk // Get redirect URL
        val token = document.select("form#$chapterID input").attr("value") // Get token
        val method = document.select("form#$chapterID").attr("method") // Check POST or GET

        val getHeaders = headersBuilder()
            .add("User-Agent", userAgent)
            .add("Referer", chapterURL)
            .add("Content-Type", "application/x-www-form-urlencoded")
            .build()

        val formBody = when (method) {
            "GET" -> null
            "POST" -> FormBody.Builder()
                .add("_token", token)
                .build()
            else -> throw UnsupportedOperationException("TMO Unknown method. Open GitHub issue")
        }

        val url = getBuilder(geturl, getHeaders, formBody, method).substringBeforeLast("/") + "/${getPageMethod()}"
        // Get /cascade instead of /paginated to get all pages at once

        val headers = headersBuilder()
            .add("User-Agent", userAgent)
            .add("Referer", "$baseUrl/library/manga/")
            .build()

        return GET(url, headers)
    }

    override fun pageListParse(document: Document): List<Page> = mutableListOf<Page>().apply {
        if (getPageMethod() == "cascade") {
            document.select("img.viewer-img").forEach {
                add(Page(size, "", it.let {
                    if (it.hasAttr("data-src"))
                        it.attr("abs:data-src") else it.attr("abs:src")
                }))
            }
        } else {
            val pageList = document.select("#viewer-pages-select").first().select("option").map { it.attr("value").toInt() }
            val url = document.baseUri()
            pageList.forEach {
                add(Page(it, "$url/$it"))
            }
        }
    }

    override fun imageUrlParse(document: Document): String {
        return document.select("div.viewer-container > div.img-container > img.viewer-image").attr("src")
    }

    // Filters

    private class Types : UriPartFilter("Filtrar por tipo", arrayOf(
        Pair("Ver todo", ""),
        Pair("Manga", "manga"),
        Pair("Manhua", "manhua"),
        Pair("Manhwa", "manhwa"),
        Pair("Novela", "novel"),
        Pair("One shot", "one_shot"),
        Pair("Doujinshi", "doujinshi"),
        Pair("Oel", "oel")
    ))

    private class Demography : UriPartFilter("Filtrar por demografía", arrayOf(
        Pair("Ver todo", ""),
        Pair("Seinen", "seinen"),
        Pair("Shoujo", "shoujo"),
        Pair("Shounen", "shounen"),
        Pair("Josei", "josei"),
        Pair("Kodomo", "kodomo")
    ))

    private class FilterBy : UriPartFilter("Campo de orden", arrayOf(
        Pair("Título", "title"),
        Pair("Autor", "author"),
        Pair("Compañia", "company")
    ))

    class SortBy : Filter.Sort(
        "Ordenar por",
        SORTABLES.map { it.first }.toTypedArray(),
        Selection(0, false)
    )

    private class WebcomicFilter : Filter.TriState("Webcomic")
    private class FourKomaFilter : Filter.TriState("Yonkoma")
    private class AmateurFilter : Filter.TriState("Amateur")
    private class EroticFilter : Filter.TriState("Erótico")

    private class Genre(name: String, val id: String) : Filter.CheckBox(name)
    private class GenreList(genres: List<Genre>) : Filter.Group<Genre>("Filtrar por géneros", genres)

    override fun getFilterList() = FilterList(
        Types(),
        Demography(),
        Filter.Separator(),
        FilterBy(),
        SortBy(),
        Filter.Separator(),
        WebcomicFilter(),
        FourKomaFilter(),
        AmateurFilter(),
        EroticFilter(),
        GenreList(getGenreList())
    )

    // Array.from(document.querySelectorAll('#books-genders .col-auto .custom-control'))
    // .map(a => `Genre("${a.querySelector('label').innerText}", "${a.querySelector('input').value}")`).join(',\n')
    // on https://tumangaonline.me/library
    // Last revision 13/04/2020
    private fun getGenreList() = listOf(
        Genre("Acción", "1"),
        Genre("Aventura", "2"),
        Genre("Comedia", "3"),
        Genre("Drama", "4"),
        Genre("Recuentos de la vida", "5"),
        Genre("Ecchi", "6"),
        Genre("Fantasia", "7"),
        Genre("Magia", "8"),
        Genre("Sobrenatural", "9"),
        Genre("Horror", "10"),
        Genre("Misterio", "11"),
        Genre("Psicológico", "12"),
        Genre("Romance", "13"),
        Genre("Ciencia Ficción", "14"),
        Genre("Thriller", "15"),
        Genre("Deporte", "16"),
        Genre("Girls Love", "17"),
        Genre("Boys Love", "18"),
        Genre("Harem", "19"),
        Genre("Mecha", "20"),
        Genre("Supervivencia", "21"),
        Genre("Reencarnación", "22"),
        Genre("Gore", "23"),
        Genre("Apocalíptico", "24"),
        Genre("Tragedia", "25"),
        Genre("Vida Escolar", "26"),
        Genre("Historia", "27"),
        Genre("Militar", "28"),
        Genre("Policiaco", "29"),
        Genre("Crimen", "30"),
        Genre("Superpoderes", "31"),
        Genre("Vampiros", "32"),
        Genre("Artes Marciales", "33"),
        Genre("Samurái", "34"),
        Genre("Género Bender", "35"),
        Genre("Realidad Virtual", "36"),
        Genre("Ciberpunk", "37"),
        Genre("Musica", "38"),
        Genre("Parodia", "39"),
        Genre("Animación", "40"),
        Genre("Demonios", "41"),
        Genre("Familia", "42"),
        Genre("Extranjero", "43"),
        Genre("Niños", "44"),
        Genre("Realidad", "45"),
        Genre("Telenovela", "46"),
        Genre("Guerra", "47"),
        Genre("Oeste", "48")
    )

    private open class UriPartFilter(displayName: String, val vals: Array<Pair<String, String>>) :
        Filter.Select<String>(displayName, vals.map { it.first }.toTypedArray()) {
        fun toUriPart() = vals[state].second
    }

    // Preferences Code

    private val preferences: SharedPreferences by lazy {
        Injekt.get<Application>().getSharedPreferences("source_$id", 0x0000)
    }

    override fun setupPreferenceScreen(screen: androidx.preference.PreferenceScreen) {
        val deduppref = androidx.preference.ListPreference(screen.context).apply {
            key = DEDUP_PREF_Title
            title = DEDUP_PREF_Title
            entries = arrayOf("All scanlators", "One scanlator per chapter")
            entryValues = arrayOf("all", "one")
            summary = "%s"

            setOnPreferenceChangeListener { _, newValue ->
                val selected = newValue as String
                val index = this.findIndexOfValue(selected)
                val entry = entryValues[index] as String
                preferences.edit().putString(DEDUP_PREF, entry).commit()
            }
        }

        val pageMethod = androidx.preference.ListPreference(screen.context).apply {
            key = PAGEGET_PREF_Title
            title = PAGEGET_PREF_Title
            entries = arrayOf("Cascada", "Paginada")
            entryValues = arrayOf("cascade", "paginated")
            summary = "%s"

            setOnPreferenceChangeListener { _, newValue ->
                val selected = newValue as String
                val index = this.findIndexOfValue(selected)
                val entry = entryValues[index] as String
                preferences.edit().putString(PAGEGET_PREF, entry).commit()
            }
        }

        screen.addPreference(deduppref)
        screen.addPreference(pageMethod)
    }

    override fun setupPreferenceScreen(screen: PreferenceScreen) {
        val deduppref = ListPreference(screen.context).apply {
            key = DEDUP_PREF_Title
            title = DEDUP_PREF_Title
            entries = arrayOf("All scanlators", "One scanlator per chapter")
            entryValues = arrayOf("all", "one")
            summary = "%s"

            setOnPreferenceChangeListener { _, newValue ->
                val selected = newValue as String
                val index = this.findIndexOfValue(selected)
                val entry = entryValues[index] as String
                preferences.edit().putString(DEDUP_PREF, entry).commit()
            }
        }

        val pageMethod = ListPreference(screen.context).apply {
            key = PAGEGET_PREF_Title
            title = PAGEGET_PREF_Title
            entries = arrayOf("Cascada", "Paginada")
            entryValues = arrayOf("cascade", "paginated")
            summary = "%s"

            setOnPreferenceChangeListener { _, newValue ->
                val selected = newValue as String
                val index = this.findIndexOfValue(selected)
                val entry = entryValues[index] as String
                preferences.edit().putString(PAGEGET_PREF, entry).commit()
            }
        }

        screen.addPreference(deduppref)
        screen.addPreference(pageMethod)
    }

    private fun getduppref() = preferences.getString(DEDUP_PREF, "all")
    private fun getPageMethod() = preferences.getString(PAGEGET_PREF, "cascade")

    companion object {
        private const val DEDUP_PREF_Title = "Chapter List Scanlator Preference"
        private const val DEDUP_PREF = "deduppref"
        private const val PAGEGET_PREF_Title = "Método para obtener imágenes"
        private const val PAGEGET_PREF = "pagemethodpref"

        private val SORTABLES = listOf(
            Pair("Me gusta", "likes_count"),
            Pair("Alfabético", "alphabetically"),
            Pair("Puntuación", "score"),
            Pair("Creación", "creation"),
            Pair("Fecha estreno", "release_date"),
            Pair("Núm. Capítulos", "num_chapters")
        )
    }
}
