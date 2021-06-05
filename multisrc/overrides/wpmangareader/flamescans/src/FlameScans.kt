package eu.kanade.tachiyomi.extension.en.flamescans

import eu.kanade.tachiyomi.lib.ratelimit.RateLimitInterceptor
import eu.kanade.tachiyomi.multisrc.wpmangareader.WPMangaReader
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class FlameScans : WPMangaReader("Flame Scans", "https://flamescans.org", "en", "/series") {
    private val rateLimitInterceptor = RateLimitInterceptor(2)

    override val client: OkHttpClient = network.cloudflareClient.newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addNetworkInterceptor(rateLimitInterceptor)
        .build()
}
