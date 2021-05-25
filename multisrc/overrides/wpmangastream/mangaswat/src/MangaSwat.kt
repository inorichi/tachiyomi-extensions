package eu.kanade.tachiyomi.extension.ar.mangaswat

import eu.kanade.tachiyomi.multisrc.wpmangastream.WPMangaStream
import eu.kanade.tachiyomi.source.model.SChapter
import org.jsoup.nodes.Element
import eu.kanade.tachiyomi.lib.ratelimit.RateLimitInterceptor
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

class MangaSwat : WPMangaStream("MangaSwat", "https://mangaswat.com", "ar") {
    private val rateLimitInterceptor = RateLimitInterceptor(4)

    override val client: OkHttpClient = network.cloudflareClient.newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addNetworkInterceptor(rateLimitInterceptor)
        .build()

    override fun chapterFromElement(element: Element): SChapter = super.chapterFromElement(element).apply { name = name.removeSuffix(" free") }

    override fun getFilterList() = FilterList(
        StatusFilter(),
        TypeFilter(),
        SortByFilter(),
        GenreListFilter(getGenrePairs())
    )

    private class GenreListFilter(pairs: Array<Pair<String, String>>) : UriPartFilter("Genre", pairs)

    private fun getGenrePairs() = arrayOf(
        Pair("<--->", ""),
        Pair("آلات", "%d8%a2%d9%84%d8%a7%d8%aa"),
        Pair("أكشن", "%d8%a3%d9%83%d8%b4%d9%86"),
        Pair("إثارة", "%d8%a5%d8%ab%d8%a7%d8%b1%d8%a9"),
        Pair("إعادة", "%d8%a5%d8%b9%d8%a7%d8%af%d8%a9-%d8%a5%d8%ad%d9%8a%d8%a7%d8%a1"),
        Pair("الحياة", "%d8%a7%d9%84%d8%ad%d9%8a%d8%a7%d8%a9-%d8%a7%d9%84%d9%85%d8%af%d8%b1%d8%b3%d9%8a%d8%a9"),
        Pair("الحياة", "%d8%a7%d9%84%d8%ad%d9%8a%d8%a7%d8%a9-%d8%a7%d9%84%d9%8a%d9%88%d9%85%d9%8a%d8%a9"),
        Pair("العاب", "%d8%a7%d9%84%d8%b9%d8%a7%d8%a8-%d9%81%d9%8a%d8%af%d9%8a%d9%88"),
        Pair("ايتشي", "%d8%a7%d9%8a%d8%aa%d8%b4%d9%8a"),
        Pair("ايسكاي", "%d8%a7%d9%8a%d8%b3%d9%83%d8%a7%d9%8a"),
        Pair("بالغ", "%d8%a8%d8%a7%d9%84%d8%ba"),
        Pair("تاريخي", "%d8%aa%d8%a7%d8%b1%d9%8a%d8%ae%d9%8a"),
        Pair("تراجيدي", "%d8%aa%d8%b1%d8%a7%d8%ac%d9%8a%d8%af%d9%8a"),
        Pair("جوسيه", "%d8%ac%d9%88%d8%b3%d9%8a%d9%87"),
        Pair("جيندر", "%d8%ac%d9%8a%d9%86%d8%af%d8%b1-%d8%a8%d9%86%d8%af%d8%b1"),
        Pair("حربي", "%d8%ad%d8%b1%d8%a8%d9%8a"),
        Pair("حريم", "%d8%ad%d8%b1%d9%8a%d9%85"),
        Pair("خارق", "%d8%ae%d8%a7%d8%b1%d9%82-%d9%84%d9%84%d8%b7%d8%a8%d9%8a%d8%b9%d8%a9"),
        Pair("خيال", "%d8%ae%d9%8a%d8%a7%d9%84"),
        Pair("خيال", "%d8%ae%d9%8a%d8%a7%d9%84-%d8%b9%d9%84%d9%85%d9%8a"),
        Pair("دراما", "%d8%af%d8%b1%d8%a7%d9%85%d8%a7"),
        Pair("دموي", "%d8%af%d9%85%d9%88%d9%8a"),
        Pair("رعب", "%d8%b1%d8%b9%d8%a8"),
        Pair("رومانسي", "%d8%b1%d9%88%d9%85%d8%a7%d9%86%d8%b3%d9%8a"),
        Pair("رياضة", "%d8%b1%d9%8a%d8%a7%d8%b6%d8%a9"),
        Pair("زمكاني", "%d8%b2%d9%85%d9%83%d8%a7%d9%86%d9%8a"),
        Pair("زومبي", "%d8%b2%d9%88%d9%85%d8%a8%d9%8a"),
        Pair("سحر", "%d8%b3%d8%ad%d8%b1"),
        Pair("سينين", "%d8%b3%d9%8a%d9%86%d9%8a%d9%86"),
        Pair("شريحة", "%d8%b4%d8%b1%d9%8a%d8%ad%d8%a9-%d9%85%d9%86-%d8%a7%d9%84%d8%ad%d9%8a%d8%a7%d8%a9"),
        Pair("شوجو", "%d8%b4%d9%88%d8%ac%d9%88"),
        Pair("شونين", "%d8%b4%d9%88%d9%86%d9%8a%d9%86"),
        Pair("شياطين", "%d8%b4%d9%8a%d8%a7%d8%b7%d9%8a%d9%86"),
        Pair("طبخ", "%d8%b7%d8%a8%d8%ae"),
        Pair("طبي", "%d8%b7%d8%a8%d9%8a"),
        Pair("غموض", "%d8%ba%d9%85%d9%88%d8%b6"),
        Pair("فانتازي", "%d9%81%d8%a7%d9%86%d8%aa%d8%a7%d8%b2%d9%8a"),
        Pair("فنون", "%d9%81%d9%86%d9%88%d9%86-%d9%82%d8%aa%d8%a7%d9%84%d9%8a%d8%a9"),
        Pair("فوق", "%d9%81%d9%88%d9%82-%d8%a7%d9%84%d8%b7%d8%a8%d9%8a%d8%b9%d8%a9"),
        Pair("قوى", "%d9%82%d9%88%d9%89-%d8%ae%d8%a7%d8%b1%d9%82%d8%a9"),
        Pair("كوميدي", "%d9%83%d9%88%d9%85%d9%8a%d8%af%d9%8a"),
        Pair("لعبة", "%d9%84%d8%b9%d8%a8%d8%a9"),
        Pair("مافيا", "%d9%85%d8%a7%d9%81%d9%8a%d8%a7"),
        Pair("مصاصى", "%d9%85%d8%b5%d8%a7%d8%b5%d9%89-%d8%a7%d9%84%d8%af%d9%85%d8%a7%d8%a1"),
        Pair("مغامرات", "%d9%85%d8%ba%d8%a7%d9%85%d8%b1%d8%a7%d8%aa"),
        Pair("ميكا", "%d9%85%d9%8a%d9%83%d8%a7"),
        Pair("نفسي", "%d9%86%d9%81%d8%b3%d9%8a"),
        Pair("وحوش", "%d9%88%d8%ad%d9%88%d8%b4"),
        Pair("ويب-تون", "%d9%88%d9%8a%d8%a8-%d8%aa%d9%88%d9%86")
    )
}
