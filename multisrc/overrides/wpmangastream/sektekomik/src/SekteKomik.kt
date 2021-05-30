package eu.kanade.tachiyomi.extension.id.sektekomik

import eu.kanade.tachiyomi.lib.ratelimit.RateLimitInterceptor
import eu.kanade.tachiyomi.multisrc.wpmangastream.WPMangaStream
import okhttp3.Headers
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class SekteKomik : WPMangaStream("Sekte Komik", "https://sektekomik.com", "id") {
    // Formerly "Sekte Komik (WP Manga Stream)"
    override val id = 7866629035053218469

    private val rateLimitInterceptor = RateLimitInterceptor(4)

    override val client: OkHttpClient = network.cloudflareClient.newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addNetworkInterceptor(rateLimitInterceptor)
        .build()

    override fun headersBuilder(): Headers.Builder = Headers.Builder()
        .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        .add("Accept-language", "en-US,en;q=0.9")
        .add("Referer", baseUrl)
}
