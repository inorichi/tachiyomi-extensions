package eu.kanade.tachiyomi.extension.en.oglaf

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import rx.Observable
import org.jsoup.Jsoup

class Oglaf : ParsedHttpSource() {

    override val name = "Oglaf"

    override val baseUrl = "https://www.oglaf.com"

    override val lang = "en"

    override val supportsLatest = false

    override fun fetchPopularManga(page: Int): Observable<MangasPage> {
        val manga = SManga.create().apply {
            title = "Oglaf"
            artist = "Trudy Cooper & Doug Bayne"
            author = "Trudy Cooper & Doug Bayne"
            status = SManga.ONGOING
            url = "/archive/"
            description = "Filth and other Fantastical Things in handy webcomic form."
            thumbnail_url = "https://i.ibb.co/tzY0VQ9/oglaf.png"
        }

        return Observable.just(MangasPage(arrayListOf(manga), false))
    }

    override fun fetchSearchManga(page: Int, query: String, filters: FilterList): Observable<MangasPage> = Observable.empty()

    override fun fetchMangaDetails(manga: SManga) = Observable.just(manga)

    override fun chapterListParse(response: Response): List<SChapter> {
        val chapterList = super.chapterListParse(response).distinct()
        return chapterList.mapIndexed {
            i, ch -> ch.apply { chapter_number = chapterList.size.toFloat() - i }
        }
    }

    override fun chapterListSelector() = "a:has(img[width=400])"

    override fun chapterFromElement(element: Element): SChapter {
        val nameRegex = """/(.*)/""".toRegex()
        val chapter = SChapter.create()
        chapter.url = element.attr("href")
        chapter.name = nameRegex.find(element.attr("href"))!!.groupValues[1]
        return chapter
    }

    override fun pageListParse(document: Document): List<Page> {
        val urlRegex = """/.*/\d*/""".toRegex()
        val imageUrl = document.select("img#strip").attr("src")
        val pages = mutableListOf<Page>()
        pages.add(Page(0, "", imageUrl))
        val next = document.select("a[rel=next]").attr("href")
        if (urlRegex.matches(next)) {
            val nextPage = Jsoup.connect(baseUrl + next).get()
            pages.addAll(pageListParse(nextPage))
        }
        return pages
    }

    override fun imageUrlParse(document: Document) = throw Exception("Not used")

    override fun popularMangaSelector(): String = throw Exception("Not used")

    override fun searchMangaFromElement(element: Element): SManga = throw Exception("Not used")

    override fun searchMangaNextPageSelector(): String? = throw Exception("Not used")

    override fun searchMangaSelector(): String = throw Exception("Not used")

    override fun popularMangaRequest(page: Int): Request = throw Exception("Not used")

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request = throw Exception("Not used")

    override fun popularMangaNextPageSelector(): String? = throw Exception("Not used")

    override fun popularMangaFromElement(element: Element): SManga = throw Exception("Not used")

    override fun mangaDetailsParse(document: Document): SManga = throw Exception("Not used")

    override fun latestUpdatesNextPageSelector(): String? = throw Exception("Not used")

    override fun latestUpdatesFromElement(element: Element): SManga = throw Exception("Not used")

    override fun latestUpdatesRequest(page: Int): Request = throw Exception("Not used")

    override fun latestUpdatesSelector(): String = throw Exception("Not used")

}
