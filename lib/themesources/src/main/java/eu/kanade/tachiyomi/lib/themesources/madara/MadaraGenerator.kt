package eu.kanade.tachiyomi.lib.themesources.madara

import eu.kanade.tachiyomi.lib.themesources.ThemeSourceGenerator
import eu.kanade.tachiyomi.lib.themesources.ThemeSourceGenerator.Companion.SingleLangThemeSourceData
import eu.kanade.tachiyomi.lib.themesources.ThemeSourceGenerator.Companion.MultiLangThemeSourceData

class MadaraGenerator : ThemeSourceGenerator {

    override val themePkg = "madara"

    override val themeClass = "Madara"

    override val baseVersionCode: Int = 1

    override val sources = listOf(
        SingleLangThemeSourceData("ATM-Subs", "https://atm-subs.fr", "fr", className = "ATMSubs", pkgName = "atmsubs"),
        SingleLangThemeSourceData("Adonis Fansub", "https://manga.adonisfansub.com", "tr"),
        SingleLangThemeSourceData("AkuManga", "https://akumanga.com", "ar"),
        SingleLangThemeSourceData("AllPornComic", "https://allporncomic.com", "en"),
        SingleLangThemeSourceData("Aloalivn", "https://aloalivn.com", "en"),
        SingleLangThemeSourceData("AniMangaEs", "http://animangaes.com", "en"),
        SingleLangThemeSourceData("Agent of Change Translations", "https://aoc.moe", "en"),
        SingleLangThemeSourceData("ApollComics", "https://apollcomics.xyz", "es"),
        SingleLangThemeSourceData("Arang Scans", "https://www.arangscans.com", "en"),
        SingleLangThemeSourceData("ArazNovel", "https://www.araznovel.com", "tr"),
        SingleLangThemeSourceData("Asgard Team", "https://www.asgard1team.com", "ar"),
        SingleLangThemeSourceData("Astral Library", "https://www.astrallibrary.net", "en"),
        SingleLangThemeSourceData("Bakaman", "https://bakaman.net", "th"),
        SingleLangThemeSourceData("BestManga", "https://bestmanga.club", "ru"),
        SingleLangThemeSourceData("BestManhua", "https://bestmanhua.com", "en"),
        SingleLangThemeSourceData("BoysLove", "https://boyslove.me", "en"),
        SingleLangThemeSourceData("CatOnHeadTranslations", "https://catonhead.com", "en"),
        SingleLangThemeSourceData("CAT-translator", "https://cat-translator.com", "th", className = "CatTranslator", pkgName = "cattranslator"),
        SingleLangThemeSourceData("Chibi Manga", "https://www.cmreader.info", "en"),
        SingleLangThemeSourceData("ComicKiba", "https://comickiba.com", "en"),
        SingleLangThemeSourceData("Comics Valley", "https://comicsvalley.com", "hi", isNsfw = true),
    )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            MadaraGenerator().createAll()
        }
    }
}
