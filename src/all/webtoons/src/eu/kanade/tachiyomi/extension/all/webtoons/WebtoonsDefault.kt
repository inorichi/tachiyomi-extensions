package eu.kanade.tachiyomi.extension.all.webtoons

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.*

open class WebtoonsDefault(override val lang: String) : Webtoons(lang) {

    override fun chapterListSelector() = "ul#_episodeList > li[id*=episode]"

    override fun chapterFromElement(element: Element): SChapter {
        val urlElement = element.select("a")

        val chapter = SChapter.create()
        chapter.setUrlWithoutDomain(urlElement.attr("href"))
        chapter.name = element.select("a > div.row > div.info > p.sub_title > span.ellipsis").text()
        val select = element.select("a > div.row > div.num")
        if (select.isNotEmpty()) {
            chapter.name += " Ch. " + select.text().substringAfter("#")
        }
        if (element.select(".ico_bgm").isNotEmpty()) {
            chapter.name += " ♫"
        }
        chapter.date_upload = element.select("a > div.row > div.info > p.date").text()?.let { SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).parse(it).time } ?: 0
        return chapter
    }

    override fun chapterListRequest(manga: SManga) = GET("http://m.webtoons.com" + manga.url, mobileHeaders)

    override fun pageListParse(document: Document) = document.select("div#_imageList > img").mapIndexed { i, element -> Page(i, "", element.attr("data-url")) }
}