package eu.kanade.tachiyomi.multisrc.weebreader

import generator.ThemeSourceData.SingleLang
import generator.ThemeSourceGenerator

class WeebreaderGenerator : ThemeSourceGenerator {

    override val themePkg = "weebreader"

    override val themeClass = "Weebreader"

    override val baseVersionCode: Int = 1

    override val sources = listOf(
        SingleLang("Arang Scans", "https://arangscans.com", "en", overrideVersionCode = 10),
    )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            WeebreaderGenerator().createAll()
        }
    }
}
