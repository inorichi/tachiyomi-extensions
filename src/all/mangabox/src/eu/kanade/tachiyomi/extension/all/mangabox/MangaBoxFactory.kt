package eu.kanade.tachiyomi.extension.all.mangabox

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.POST
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceFactory
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.util.asJsoup
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.Locale

class MangaBoxFactory : SourceFactory {
    override fun createSources(): List<Source> = listOf(
        Mangakakalot(),
        Manganelo(),
        Mangabat(),
        MangaOnl()
        //ChapterManga()
    )
}

/**
 * Base MangaBox class allows for genre search using query parameters in URLs
 * MangaBoxPathedGenres class extends base class, genre search only uses path segments in URLs
 */

abstract class MangaBoxPathedGenres(
    name: String,
    baseUrl: String,
    lang: String,
    dateformat: SimpleDateFormat = SimpleDateFormat("MMM-dd-yy", Locale.ENGLISH)
) : MangaBox(name, baseUrl, lang, dateformat) {
    override fun getFilterList() = FilterList(
        Filter.Header("NOTE: Ignored if using text search!"),
        Filter.Separator(),
        GenreFilter(getGenrePairs())
    )
    class GenreFilter(genrePairs: Array<Pair<String, String>>) : UriPartFilter("Category", genrePairs)
    // Pair("path_segment/", "display name")
    abstract fun getGenrePairs(): Array<Pair<String, String>>
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return if (query.isNotBlank()) {
            GET("$baseUrl/$simpleQueryPath${normalizeSearchQuery(query)}?page=$page", headers)
        } else {
            var url = "$baseUrl/"

            filters.forEach { filter ->
                when (filter) {
                    is GenreFilter -> {
                        url += filter.toUriPart()
                    }
                }
            }
            GET(url + page, headers)
        }
    }
}

class Mangakakalot : MangaBox("Mangakakalot", "https://mangakakalots.com", "en") {
    override fun searchMangaSelector() = "${super.searchMangaSelector()}, div.list-truyen-item-wrap"
}

class Manganelo : MangaBoxPathedGenres("Manganelo", "https://manganelo.com", "en") {
    // Nelo's date format is part of the base class
    override fun popularMangaRequest(page: Int): Request = GET("$baseUrl/genre-all/$page?type=topview", headers)
    override fun popularMangaSelector() = "div.content-genres-item"
    override val latestUrlPath = "genre-all/"
    override fun searchMangaSelector() = "div.search-story-item, div.content-genres-item"
    override fun getGenrePairs() = arrayOf(
        Pair("genre-all/", "All"),
        Pair("genre-2/", "Action"),
        Pair("genre-3/", "Adult"),
        Pair("genre-4/", "Adventure"),
        Pair("genre-6/", "Comedy"),
        Pair("genre-7/", "Cooking"),
        Pair("genre-9/", "Doujinshi"),
        Pair("genre-10/", "Drama"),
        Pair("genre-11/", "Ecchi"),
        Pair("genre-12/", "Fantasy"),
        Pair("genre-13/", "Gender bender"),
        Pair("genre-14/", "Harem"),
        Pair("genre-15/", "Historical"),
        Pair("genre-16/", "Horror"),
        Pair("genre-45/", "Isekai"),
        Pair("genre-17/", "Josei"),
        Pair("genre-44/", "Manhua"),
        Pair("genre-43/", "Manhwa"),
        Pair("genre-19/", "Martial arts"),
        Pair("genre-20/", "Mature"),
        Pair("genre-21/", "Mecha"),
        Pair("genre-22/", "Medical"),
        Pair("genre-24/", "Mystery"),
        Pair("genre-25/", "One shot"),
        Pair("genre-26/", "Psychological"),
        Pair("genre-27/", "Romance"),
        Pair("genre-28/", "School life"),
        Pair("genre-29/", "Sci fi"),
        Pair("genre-30/", "Seinen"),
        Pair("genre-31/", "Shoujo"),
        Pair("genre-32/", "Shoujo ai"),
        Pair("genre-33/", "Shounen"),
        Pair("genre-34/", "Shounen ai"),
        Pair("genre-35/", "Slice of life"),
        Pair("genre-36/", "Smut"),
        Pair("genre-37/", "Sports"),
        Pair("genre-38/", "Supernatural"),
        Pair("genre-39/", "Tragedy"),
        Pair("genre-40/", "Webtoons"),
        Pair("genre-41/", "Yaoi"),
        Pair("genre-42/", "Yuri")
    )
}

class Mangabat : MangaBox("Mangabat", "https://mangabat.com", "en", SimpleDateFormat("MMM dd,yy", Locale.ENGLISH)) {
    override fun popularMangaRequest(page: Int): Request = GET("$baseUrl/manga-list-all/$page?type=topview", headers)
    override fun popularMangaSelector() = "div.list-story-item"
    override val latestUrlPath = "manga-list-all/"
    override fun searchMangaSelector() = "div.list-story-item"
}

class MangaOnl : MangaBoxPathedGenres("MangaOnl", "https://mangaonl.com", "en") {
    override val popularUrlPath = "story-list-ty-topview-st-all-ca-all-"
    override val latestUrlPath = "story-list-ty-latest-st-all-ca-all-"
    override fun popularMangaSelector() = "div.story_item"
    override val mangaDetailsMainSelector = "div.panel_story_info, ${super.mangaDetailsMainSelector}" //Some manga link to Nelo
    override val thumbnailSelector = "img.story_avatar, ${super.thumbnailSelector}"
    override val descriptionSelector = "div.panel_story_info_description, ${super.descriptionSelector}"
    override fun chapterListSelector() = "div.chapter_list_title + ul li, ${super.chapterListSelector()}"
    override val pageListSelector = "div.container_readchapter img, ${super.pageListSelector}"
    override fun getGenrePairs() = arrayOf(
        Pair("story-list-ty-latest-st-all-ca-all-", "ALL"),
        Pair("story-list-ty-latest-st-all-ca-2-", "Action"),
        Pair("story-list-ty-latest-st-all-ca-3-", "Adult"),
        Pair("story-list-ty-latest-st-all-ca-4-", "Adventure"),
        Pair("story-list-ty-latest-st-all-ca-6-", "Comedy"),
        Pair("story-list-ty-latest-st-all-ca-7-", "Cooking"),
        Pair("story-list-ty-latest-st-all-ca-9-", "Doujinshi"),
        Pair("story-list-ty-latest-st-all-ca-10-", "Drama"),
        Pair("story-list-ty-latest-st-all-ca-11-", "Ecchi"),
        Pair("story-list-ty-latest-st-all-ca-12-", "Fantasy"),
        Pair("story-list-ty-latest-st-all-ca-13-", "Gender bender"),
        Pair("story-list-ty-latest-st-all-ca-14-", "Harem"),
        Pair("story-list-ty-latest-st-all-ca-15-", "Historical"),
        Pair("story-list-ty-latest-st-all-ca-16-", "Horror"),
        Pair("story-list-ty-latest-st-all-ca-45-", "Isekai"),
        Pair("story-list-ty-latest-st-all-ca-17-", "Josei"),
        Pair("story-list-ty-latest-st-all-ca-43-", "Manhwa"),
        Pair("story-list-ty-latest-st-all-ca-44-", "Manhua"),
        Pair("story-list-ty-latest-st-all-ca-19-", "Martial arts"),
        Pair("story-list-ty-latest-st-all-ca-20-", "Mature"),
        Pair("story-list-ty-latest-st-all-ca-21-", "Mecha"),
        Pair("story-list-ty-latest-st-all-ca-22-", "Medical"),
        Pair("story-list-ty-latest-st-all-ca-24-", "Mystery"),
        Pair("story-list-ty-latest-st-all-ca-25-", "One shot"),
        Pair("story-list-ty-latest-st-all-ca-26-", "Psychological"),
        Pair("story-list-ty-latest-st-all-ca-27-", "Romance"),
        Pair("story-list-ty-latest-st-all-ca-28-", "School life"),
        Pair("story-list-ty-latest-st-all-ca-29-", "Sci fi"),
        Pair("story-list-ty-latest-st-all-ca-30-", "Seinen"),
        Pair("story-list-ty-latest-st-all-ca-31-", "Shoujo"),
        Pair("story-list-ty-latest-st-all-ca-32-", "Shoujo ai"),
        Pair("story-list-ty-latest-st-all-ca-33-", "Shounen"),
        Pair("story-list-ty-latest-st-all-ca-34-", "Shounen ai"),
        Pair("story-list-ty-latest-st-all-ca-35-", "Slice of life"),
        Pair("story-list-ty-latest-st-all-ca-36-", "Smut"),
        Pair("story-list-ty-latest-st-all-ca-37-", "Sports"),
        Pair("story-list-ty-latest-st-all-ca-38-", "Supernatural"),
        Pair("story-list-ty-latest-st-all-ca-39-", "Tragedy"),
        Pair("story-list-ty-latest-st-all-ca-40-", "Webtoons"),
        Pair("story-list-ty-latest-st-all-ca-41-", "Yaoi"),
        Pair("story-list-ty-latest-st-all-ca-42-", "Yuri")
    )
}

class ChapterManga : MangaBox("ChapterManga", "https://chaptermanga.com", "en", SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)) {
    override val popularUrlPath = "hot-manga-page-"
    override val latestUrlPath = "read-latest-manga-page-"
    override fun chapterListRequest(manga: SManga): Request {
        val response = client.newCall(GET(baseUrl + manga.url, headers)).execute()
        val cookie = response.headers("set-cookie")
            .filter{ it.contains("laravel_session") }
            .map{ it.substringAfter("=").substringBefore(";") }
        val document = response.asJsoup()
        val token = document.select("meta[name=\"csrf-token\"]").attr("content")
        val script = document.select("script:containsData(manga_slug)").first()
        val mangaSlug = script.data().substringAfter("manga_slug : \'").substringBefore("\'")
        val mangaId = script.data().substringAfter("manga_id : \'").substringBefore("\'")
        val tokenHeaders = headers.newBuilder()
            .add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .add("X-CSRF-Token", token)
            .add("Cookie", cookie.toString())
            .build()
        val body = RequestBody.create(null, "manga_slug=$mangaSlug&manga_id=$mangaId")

        return POST("$baseUrl/get-chapter-list", tokenHeaders, body)
    }
    override fun chapterListSelector() = "div.row"
    override fun chapterFromElement(element: Element): SChapter = super.chapterFromElement(element).apply {
        chapter_number = Regex("""[Cc]hapter\s\d*""").find(name)?.value?.substringAfter(" ")?.toFloatOrNull() ?: 0F
    }
    // TODO chapterlistparse -- default chapter order could be better
    override fun getFilterList() = FilterList()
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val site = baseUrl.substringAfter("//")
        val searchHeaders = headers.newBuilder().add("Content-Type", "application/x-www-form-urlencoded").build()
        val body = RequestBody.create(null, "q=site%3A$site+inurl%3A$site%2Fread-manga+${query.replace(" ", "+")}&b=&kl=us-en")

        return POST("https://duckduckgo.com/html/", searchHeaders, body)
    }
    override fun searchMangaParse(response: Response): MangasPage {
        val mangas = response.asJsoup().select(searchMangaSelector())
            .filter{ it.text().startsWith("Read") }
            .map{ searchMangaFromElement(it) }

        return MangasPage(mangas, false)
    }
    override fun searchMangaSelector() = "div.result h2 a"
    override fun searchMangaFromElement(element: Element): SManga {
        return SManga.create().apply {
            title = element.text().substringAfter("Read").substringBeforeLast("online").trim()
            setUrlWithoutDomain(element.attr("href"))
        }
    }
}

