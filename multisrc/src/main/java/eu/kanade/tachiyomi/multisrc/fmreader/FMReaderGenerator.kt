package eu.kanade.tachiyomi.multisrc.fmreader

import eu.kanade.tachiyomi.multisrc.ThemeSourceData.MultiLang
import eu.kanade.tachiyomi.multisrc.ThemeSourceData.SingleLang
import eu.kanade.tachiyomi.multisrc.ThemeSourceGenerator

class FMReaderGenerator : ThemeSourceGenerator {

    override val themePkg = "fmreader"

    override val themeClass = "FMReader"

    override val baseVersionCode: Int = 1

    /** For future sources: when testing and popularMangaRequest() returns a Jsoup error instead of results
     *  most likely the fix is to override popularMangaNextPageSelector()   */

    override val sources = listOf(
            SingleLang("18LHPlus", "https://18lhplus.com", "en", className = "EighteenLHPlus"),
            SingleLang("Epik Manga", "https://www.epikmanga.com", "tr"),
            SingleLang("HanaScan (RawQQ)", "https://hanascan.com", "ja", className = "HanaScanRawQQ"),
            SingleLang("HeroScan", "https://heroscan.com", "en"),
            SingleLang("KissLove", "https://kissaway.net", "ja"),
            SingleLang("LHTranslation", "https://lhtranslation.net", "en"),
            SingleLang("Manga-TR", "https://manga-tr.com", "tr", className = "MangaTR"),
            SingleLang("ManhuaScan", "https://manhuascan.com", "en"),
            SingleLang("Manhwa18", "https://manhwa18.com", "en"),
            MultiLang("Manhwa18.net", "https://manhwa18.net", listOf("en", "ko"), className = "Manhwa18NetFactory"),
            SingleLang("ManhwaSmut", "https://manhwasmut.com", "en"),
            SingleLang("RawLH", "https://lovehug.net", "ja"),
            SingleLang("Say Truyen", "https://saytruyen.com", "vi"),
    )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FMReaderGenerator().createAll()
        }
    }
}
