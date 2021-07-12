package eu.kanade.tachiyomi.multisrc.madara

import generator.ThemeSourceData.MultiLang
import generator.ThemeSourceData.SingleLang
import generator.ThemeSourceGenerator

class MadaraGenerator : ThemeSourceGenerator {

    override val themePkg = "madara"

    override val themeClass = "Madara"

    override val baseVersionCode: Int = 7

    override val sources = listOf(
        MultiLang("Leviatan Scans", "https://leviatanscans.com", listOf("en", "es"), className = "LeviatanScansFactory", overrideVersionCode = 5),
        MultiLang("MangaForFree.net", "https://mangaforfree.net",  listOf("en", "ko", "all") , isNsfw = true, className = "MangaForFreeFactory", pkgName = "mangaforfree", overrideVersionCode = 1),
        MultiLang("Manhwa18.cc", "https://manhwa18.cc", listOf("en", "ko", "all"), isNsfw = true, className = "Manhwa18CcFactory", pkgName = "manhwa18cc"),
        SingleLang("1st Kiss Manga.love", "https://1stkissmanga.love", "en", className = "FirstKissMangaLove"),
        SingleLang("1st Kiss", "https://1stkissmanga.com", "en", className = "FirstKissManga", pkgName = "firstkissmanga", overrideVersionCode = 5),
        SingleLang("1st Kiss Manhua", "https://1stkissmanhua.com", "en", className = "FirstKissManhua", overrideVersionCode = 2),
        SingleLang("1stKissManga.Club", "https://1stkissmanga.club", "en", className = "FirstKissMangaClub"),
        SingleLang("247Manga", "https://247manga.com", "en", className = "Manga247"),
        SingleLang("24hManga", "https://24hmanga.com", "en", isNsfw = true, className = "TwentyFourhManga"),
        SingleLang("24hRomance", "https://24hromance.com", "en", className = "Romance24h"),
        SingleLang("365Manga", "https://365manga.com", "en", className = "ThreeSixtyFiveManga", overrideVersionCode = 1),
        SingleLang("AYATOON", "https://ayatoon.com", "tr", overrideVersionCode = 1),
        SingleLang("Adonis Fansub", "https://manga.adonisfansub.com", "tr", overrideVersionCode = 1),
        SingleLang("Agent of Change Translations", "https://aoc.moe", "en", overrideVersionCode = 1),
        SingleLang("AkuManga", "https://akumanga.com", "ar", overrideVersionCode = 1),
        SingleLang("AllPornComic", "https://allporncomic.com", "en", isNsfw = true),
        SingleLang("Ancient Empire Scan", "https://ancientempirescan.website", "es", overrideVersionCode = 1),
        SingleLang("Aqua Manga", "https://aquamanga.com", "en"),
        SingleLang("Anisa Manga", "https://anisamanga.com", "tr"),
        SingleLang("Anitation Arts", "https://anitationarts.org", "en"),
        SingleLang("ApollComics", "https://apollcomics.xyz", "es", overrideVersionCode = 1),
        SingleLang("ArabMkr", "https://arabmkr.me", "ar"),
        SingleLang("ArazNovel", "https://www.araznovel.com", "tr", overrideVersionCode = 1),
        SingleLang("Argos Scan", "https://argosscan.com", "pt-BR", overrideVersionCode = 2),
        SingleLang("Arthur Scan", "https://arthurscan.xyz", "pt-BR", overrideVersionCode = 1),
        SingleLang("Asgard Team", "https://www.asgard1team.com", "ar", overrideVersionCode = 1),
        SingleLang("Astral Library", "https://www.astrallibrary.net", "en", overrideVersionCode = 2),
        SingleLang("Atikrost", "https://atikrost.com", "tr", overrideVersionCode = 1),
        SingleLang("Azora", "https://azoramanga.com", "ar", overrideVersionCode = 2),
        SingleLang("BL Manhwa Club", "https://blmanhwa.club", "pt-BR", isNsfw = true, className = "BlManhwaClub", overrideVersionCode = 1),
        SingleLang("Bakaman", "https://bakaman.net", "th", overrideVersionCode = 1),
        SingleLang("Banana Mecânica", "https://leitorbm.com", "pt-BR", isNsfw = true, pkgName = "bananamecanica", className = "BananaMecanica", overrideVersionCode = 1),
        SingleLang("BestManga", "https://bestmanga.club", "ru", overrideVersionCode = 1),
        SingleLang("BestManhua", "https://bestmanhua.com", "en", overrideVersionCode = 2),
        SingleLang("BlogManga", "https://blogmanga.net", "en"),
        SingleLang("BoysLove", "https://boyslove.me", "en", overrideVersionCode = 2),
        SingleLang("CAT-translator", "https://cat-translator.com", "th", className = "CatTranslator", overrideVersionCode = 1),
        SingleLang("Café com Yaoi", "http://cafecomyaoi.com.br", "pt-BR", pkgName = "cafecomyaoi", className = "CafeComYaoi", isNsfw = true),
        SingleLang("CatOnHeadTranslations", "https://catonhead.com", "en", overrideVersionCode = 1),
        SingleLang("Cerise Scans", "https://cerisescans.com", "pt-BR", overrideVersionCode = 1),
        SingleLang("Cervo Scanlator", "https://cervoscan.xyz", "pt-BR", overrideVersionCode = 2),
        SingleLang("Chibi Manga", "https://www.cmreader.info", "en", overrideVersionCode = 1),
        SingleLang("Clover Manga", "https://clover-manga.com", "tr", overrideVersionCode = 2),
        SingleLang("ComicKiba", "https://comickiba.com", "en", overrideVersionCode = 1),
        SingleLang("Comicdom", "https://comicdom.org", "en", overrideVersionCode = 1),
        SingleLang("Comichub", "https://comichub.net", "en"),
        SingleLang("Comics Valley", "https://comicsvalley.com", "hi", isNsfw = true, overrideVersionCode = 1),
        SingleLang("ComicsWorld", "https://comicsworld.in", "hi"),
        SingleLang("CopyPasteScan", "https://copypastescan.xyz", "es", overrideVersionCode = 1),
        SingleLang("DarkYue Realm", "https://darkyuerealm.site/web", "pt-BR", pkgName = "darkyurealm", overrideVersionCode = 3),
        SingleLang("Decadence Scans", "https://reader.decadencescans.com", "en", overrideVersionCode = 1),
        SingleLang("DiamondFansub", "https://diamondfansub.com", "tr", overrideVersionCode = 1),
        SingleLang("Disaster Scans", "https://disasterscans.com", "en", overrideVersionCode = 1),
        SingleLang("Diskus Scan", "https://diskusscan.com", "pt-BR"),
        SingleLang("DoujinHentai", "https://doujinhentai.net", "es", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Dream Manga", "https://en.ruyamanga.com", "en", overrideVersionCode = 2),
        SingleLang("Drope Scan", "https://dropescan.com", "pt-BR", overrideVersionCode = 2),
        SingleLang("Esomanga", "http://esomanga.com", "tr"),
        SingleLang("Exo Scans", "https://exoscans.club", "en"),
        SingleLang("FDM Scan", "https://fdmscan.com", "pt-BR", overrideVersionCode = 2),
        SingleLang("Free Manga", "https://freemanga.me", "en", isNsfw = true, overrideVersionCode = 2),
        SingleLang("FreeWebtoonCoins", "https://freewebtooncoins.com", "en", overrideVersionCode = 1),
        SingleLang("Fudido Scanlator", "https://fudidoscan.com", "pt-BR", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Fukushuu no Yuusha", "https://fny-scantrad.com", "fr", overrideVersionCode = 2),
        SingleLang("Furio Scans", "https://furioscans.com", "pt-BR", overrideVersionCode = 2),
        SingleLang("Fênix Scanlator", "https://fenixscanlator.xyz", "pt-BR", pkgName = "fenixscanlator", className = "FenixScanlator", overrideVersionCode = 1),
        SingleLang("GalaxyDegenScans", "https://gdegenscans.xyz/", "en", overrideVersionCode = 1),
        SingleLang("Geass Hentai", "https://geasshentai.xyz", "pt-BR", isNsfw = true),
        SingleLang("Glory Scans", "https://gloryscan.com", "pt-BR", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Graze Scans", "https://grazescans.com/", "en", overrideVersionCode = 1),
        SingleLang("GuncelManga", "https://guncelmanga.com", "tr", overrideVersionCode = 1),
        SingleLang("Hades no Fansub", "https://mangareaderpro.com/es", "es"),
        SingleLang("Hades no Fansub Hentai", "https://h.mangareaderpro.com", "es", isNsfw = true),
        SingleLang("Hayalistic", "https://hayalistic.com", "tr"),
        SingleLang("Hentai20", "https://hentai20.com", "en", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Hentaidexy", "https://hentaidexy.com", "en", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Hero Manhua", "https://heromanhua.com", "en"),
        SingleLang("Heroz Scanlation", "https://herozscans.com", "en", overrideVersionCode = 1),
        SingleLang("Himera Fansub", "https://himera-fansub.com", "tr"),
        SingleLang("Hiperdex", "https://hiperdex.com", "en", isNsfw = true, overrideVersionCode = 4),
        SingleLang("hManhwa", "https://hmanhwa.com", "en", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Hscans", "https://hscans.com", "en", overrideVersionCode = 1),
        SingleLang("Hunter Fansub", "https://hunterfansub.com", "es", overrideVersionCode = 1),
        SingleLang("Ichirin No Hana Yuri", "https://ichirinnohanayuri.com.br", "pt-BR", overrideVersionCode = 3),
        SingleLang("Immortal Updates", "https://immortalupdates.com", "en", overrideVersionCode = 1),
        SingleLang("Imperfect Comics", "https://imperfectcomic.com", "en"),
        SingleLang("Império dos Otakus", "https://imperiodosotakus.tk", "pt-BR", className = "ImperioDosOtakus", overrideVersionCode = 1),
        SingleLang("InfraFandub", "https://infrafandub.xyz", "es"),
        SingleLang("IsekaiScan.com", "https://isekaiscan.com", "en", className = "IsekaiScanCom", overrideVersionCode = 2),
        SingleLang("IsekaiScanManga (unoriginal)", "https://isekaiscanmanga.com", "en", className = "IsekaiScanManga", overrideVersionCode = 1),
        SingleLang("Its Your Right Manhua", "https://itsyourightmanhua.com/", "en", overrideVersionCode = 1),
        SingleLang("JJutsuScans", "https://jjutsuscans.com", "en", overrideVersionCode = 1),
        SingleLang("KawaScans", "https://kawascans.com", "en"),
        SingleLang("KisekiManga", "https://kisekimanga.com", "en", overrideVersionCode = 1),
        SingleLang("Kissmanga.in", "https://kissmanga.in", "en", className= "KissmangaIn", overrideVersionCode = 1),
        SingleLang("KlikManga", "https://klikmanga.com", "id", overrideVersionCode = 1),
        SingleLang("Kombatch", "https://kombatch.com", "id"),
        SingleLang("Kun Manga", "https://kunmanga.com", "en", overrideVersionCode = 1),
        SingleLang("Latest Manga", "https://latestmanga.net", "en"),
        SingleLang("Levelerscans", "https://levelerscans.xyz", "en", overrideVersionCode = 1),
        SingleLang("Leviatan Scans X", "https://xxx.leviatanscans.com", "en", isNsfw = true),
        SingleLang("Lily Manga", "https://lilymanga.com", "en"),
        SingleLang("Lima Scans", "http://limascans.xyz/v2", "pt-BR", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Little Monster Scan", "https://littlemonsterscan.com.br", "pt-BR", overrideVersionCode = 2),
        SingleLang("Lolicon", "https://lolicon.mobi", "en", isNsfw = true),
        SingleLang("LovableSubs", "https://lovablesubs.com", "tr", overrideVersionCode = 1),
        SingleLang("MG Komik", "https://mgkomik.com", "id", overrideVersionCode = 2),
        SingleLang("MMScans", "https://mm-scans.com/", "en", overrideVersionCode = 1),
        SingleLang("Manga Action", "https://manga-action.com", "ar", overrideVersionCode = 1),
        SingleLang("Manga Bin", "https://mangabin.com/", "en", overrideVersionCode = 1),
        SingleLang("Manga Chill", "https://mangachill.com/", "en", overrideVersionCode = 1),
        SingleLang("Manga Crab", "https://mangacrab.com", "es"),
        SingleLang("Manga Diyari", "https://manga-diyari.com", "tr", overrideVersionCode = 1),
        SingleLang("Manga Drop Out", "https://www.mangadropout.xyz", "id", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Manga Fenix", "https://manga-fenix.com", "es", overrideVersionCode = 1),
        SingleLang("Manga Funny", "https://mangafunny.com", "en"),
        SingleLang("Manga Hentai", "https://mangahentai.me", "en", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Manga Kiss", "https://mangakiss.org", "en", overrideVersionCode = 1),
        SingleLang("Manga Land Arabic", "https://mangalandarabic.com", "ar", overrideVersionCode = 1),
        SingleLang("Manga Lord", "https://mangalord.com", "en", overrideVersionCode = 1),
        SingleLang("Manga Mitsu", "https://mangamitsu.com", "en", isNsfw = true, overrideVersionCode = 2),
        SingleLang("Manga Nine", "https://manganine.com", "en", overrideVersionCode = 1),
        SingleLang("Manga Read", "https://mangaread.co", "en", overrideVersionCode = 1),
        SingleLang("Manga Rock Team", "https://mangarockteam.com", "en", overrideVersionCode = 1),
        SingleLang("Manga Rocky", "https://mangarocky.com", "en", overrideVersionCode = 1),
        SingleLang("Manga Sky", "https://mangasky.net", "en"),
        SingleLang("Manga SY", "https://www.mangasy.com", "en", overrideVersionCode = 1),
        SingleLang("Manga Starz", "https://mangastarz.com", "ar", overrideVersionCode = 1),
        SingleLang("Manga Too", "https://mangatoo.com/", "en", overrideVersionCode = 1),
        SingleLang("Manga Weebs", "https://mangaweebs.in", "en", overrideVersionCode = 3),
        SingleLang("Manga-Online.co", "https://www.manga-online.co", "th", className = "MangaOnlineCo"),
        SingleLang("Manga-Scantrad", "https://manga-scantrad.net", "fr", className = "MangaScantrad", overrideVersionCode = 1),
        SingleLang("Manga18 Fx", "https://manga18fx.com", "en", overrideVersionCode = 1),
        SingleLang("Manga1st", "https://manga1st.com", "en", overrideVersionCode = 1),
        SingleLang("Manga1st.online", "https://manga1st.online", "en", className = "MangaFirstOnline", overrideVersionCode = 1),
        SingleLang("Manga347", "https://manga347.com", "en", overrideVersionCode = 3),
        SingleLang("Manga3S", "https://manga3s.com", "en", overrideVersionCode = 1),
        SingleLang("Manga4All", "https://manga4all.net", "en", overrideVersionCode = 3),
        SingleLang("Manga68", "https://manga68.com", "en", overrideVersionCode = 1),
        SingleLang("MangaBaz", "https://mangabaz.com", "tr"),
        SingleLang("MangaBob", "https://mangabob.com", "en", overrideVersionCode = 1),
        SingleLang("MangaBox", "https://mangabox.org", "en"),
        SingleLang("MangaClash", "https://mangaclash.com", "en", overrideVersionCode = 3),
        SingleLang("MangaCultivator", "https://mangacultivator.com", "en", overrideVersionCode = 1),
        SingleLang("MangaCV", "https://mangacv.com", "en", isNsfw = true),
        SingleLang("MangaDods", "https://www.mangadods.com", "en", overrideVersionCode = 2),
        SingleLang("MangaEffect", "https://mangaeffect.com", "en", overrideVersionCode = 1),
        SingleLang("Manga-fast.com", "https://manga-fast.com", "en", className = "Mangafastcom", overrideVersionCode = 1),
        SingleLang("MangaFort", "https://mangafort.com", "en"),
        SingleLang("MangaFoxFull", "https://mangafoxfull.com", "en"),
        SingleLang("MangaGreat", "https://mangagreat.com", "en", overrideVersionCode = 1),
        SingleLang("MangaHZ", "https://mangahz.com", "en", isNsfw = true),
        SingleLang("MangaKitsune", "https://mangakitsune.com", "en", isNsfw = true, overrideVersionCode = 4),
        SingleLang("MangaKomi", "https://mangakomi.com", "en", overrideVersionCode = 4),
        SingleLang("MangaLime", "https://mangalime.com", "en"),
        SingleLang("MangaLionz", "https://mangalionz.com", "ar"),
        SingleLang("MangaMe", "https://mangame.org", "en", overrideVersionCode = 1),
        SingleLang("MangaPL", "https://mangapl.com", "en", isNsfw = true, overrideVersionCode = 1),
        SingleLang("MangaRave", "https://www.mangarave.com", "en", overrideVersionCode = 2),
        SingleLang("MangaRead.org", "https://www.mangaread.org", "en", className = "MangaReadOrg", overrideVersionCode = 1),
        SingleLang("MangaSco", "https://mangasco.com", "en", overrideVersionCode = 1),
        SingleLang("MangaSpark", "https://mangaspark.com", "ar", overrideVersionCode = 1),
        SingleLang("MangaStein", "https://mangastein.com", "tr", overrideVersionCode = 1),
        SingleLang("MangaStic", "https://mangastic.com", "en"),
        SingleLang("MangaTone", "https://mangatone.com", "en"),
        SingleLang("MangaToRead", "https://mangatoread.com", "en"),
        SingleLang("MangaTK", "https://mangatk.com", "en"),
        SingleLang("MangaTX", "https://mangatx.com", "en", overrideVersionCode = 1),
        SingleLang("MangaTeca", "https://www.mangateca.com", "pt-BR", overrideVersionCode = 2),
        SingleLang("MangaTuli", "https://mangatuli.com", "en", isNsfw = true, overrideVersionCode = 2),
        SingleLang("MangaUS", "https://mangaus.xyz", "en", overrideVersionCode = 2),
        SingleLang("MangaVB", "https://mangavb.com", "en", isNsfw = true),
        SingleLang("MangaWise", "https://mangawise.com", "en"),
        SingleLang("MangaWT", "https://mangawt.com", "tr", overrideVersionCode = 1),
        SingleLang("MangaXP", "https://mangaxp.com", "en"),
        SingleLang("MangaYaku", "https://mangayaku.com", "id", overrideVersionCode = 1),
        SingleLang("MangaYami", "https://www.mangayami.club", "en", overrideVersionCode = 2),
        SingleLang("Mangaka3rb", "https://mangaka3rb.com", "ar"),
        SingleLang("Mangakik", "https://mangakik.com", "en"),
        SingleLang("Mangas Origines", "https://mangas-origines.fr", "fr" , true, overrideVersionCode = 1),
        SingleLang("Mangasushi", "https://mangasushi.net", "en", overrideVersionCode = 1),
        SingleLang("Mangauptocats", "https://mangauptocats.online", "th", overrideVersionCode = 1),
        SingleLang("Mangazuki.me", "https://mangazuki.me", "en", className = "MangazukiMe", overrideVersionCode = 1),
        SingleLang("Mangceh", "https://mangceh.me", "id", isNsfw = true, overrideVersionCode = 2),
        SingleLang("Manhua ES", "https://manhuaes.com", "en", overrideVersionCode = 4),
        SingleLang("Manhua Plus", "https://manhuaplus.com", "en", overrideVersionCode = 3),
        SingleLang("Manhua SY", "https://www.manhuasy.com", "en", overrideVersionCode = 1),
        SingleLang("ManhuaBox", "https://manhuabox.net", "en", overrideVersionCode = 2),
        SingleLang("ManhuaDex", "https://manhuadex.com", "en", overrideVersionCode = 1),
        SingleLang("ManhuaFast", "https://manhuafast.com", "en", overrideVersionCode = 1),
        SingleLang("ManhuaHot", "https://manhuahot.com", "en"),
        SingleLang("ManhuaPro", "https://manhuapro.com", "en", overrideVersionCode = 2),
        SingleLang("ManhuaUS", "https://manhuaus.com", "en", overrideVersionCode = 2),
        SingleLang("Manhuaga", "https://manhuaga.com", "en", overrideVersionCode = 1),
        SingleLang("Manhualo", "https://manhualo.com", "en", overrideVersionCode = 1),
        SingleLang("Manhuas.net", "https://manhuas.net", "en", className = "Manhuasnet", overrideVersionCode = 2),
        SingleLang("Manhwa Raw", "https://manhwaraw.com", "ko", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Manhwaraw.net", "https://manhwaraw.net", "en", className = "Manhwarawnet"),
        SingleLang("Manhwa.club", "https://manhwa.club", "en", className="ManwhaClub", overrideVersionCode = 2), // wrong class name for backward compatibility
        SingleLang("Manhwa18.org", "https://manhwa18.org", "en", isNsfw = true, className = "Manhwa18Org", overrideVersionCode = 1),
        SingleLang("Manhwa68", "https://manhwa68.com", "en", isNsfw = true, overrideVersionCode = 1),
        SingleLang("ManhwaBookShelf", "https://manhwabookshelf.com", "en"),
        SingleLang("Manhwafull", "https://manhwafull.com", "en"),
        SingleLang("ManhwaNelo", "https://manhwanelo.com", "en"),
        SingleLang("Manhwatop", "https://manhwatop.com", "en", overrideVersionCode = 1),
        SingleLang("Manhwahentai.me", "https://manhwahentai.me", "en", className = "ManhwahentaiMe", isNsfw = true, overrideVersionCode = 1),
        SingleLang("ManhwaWorld", "https://manhwaworld.com", "en"),
        SingleLang("ManyToon", "https://manytoon.com", "en", isNsfw = true, overrideVersionCode = 3),
        SingleLang("ManyToon.me", "https://manytoon.me", "en", isNsfw = true, className = "ManyToonMe", overrideVersionCode = 2),
        SingleLang("ManyToonClub", "https://manytoon.club", "ko", isNsfw = true, overrideVersionCode = 1),
        SingleLang("ManyComic", "https://manycomic.com", "en", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Mark Scans", "https://markscans.online", "pt-BR", overrideVersionCode = 2),
        SingleLang("MHentais", "https://mhentais.com", "pt-BR", isNsfw = true),
        SingleLang("NeoXXX Scans", "https://xxx.neoxscans.net", "pt-BR", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Midnight Mess Scans", "https://midnightmess.org", "en", isNsfw = true, overrideVersionCode = 5),
        SingleLang("Milftoon", "https://milftoon.xxx", "en", isNsfw = true, overrideVersionCode = 2),
        SingleLang("Mixed Manga", "https://mixedmanga.com", "en", overrideVersionCode = 1),
        SingleLang("Mode Scanlator", "https://modescanlator.com", "pt-BR", overrideVersionCode = 1),
        SingleLang("Moon Witch In Love", "https://moonwitchinlove.com", "pt-BR"),
        SingleLang("Mortals Groove", "https://mortalsgroove.com", "en"),
        SingleLang("Muctau", "https://muctau.com", "en"),
        SingleLang("Mystical Merries", "https://mysticalmerries.com", "en", overrideVersionCode = 1),
        SingleLang("NeatManga", "https://neatmanga.com", "en", overrideVersionCode = 1),
        SingleLang("NekoScan", "https://nekoscan.com", "en", overrideVersionCode = 1),
        SingleLang("NekoBreaker Scan", "https://nekobreakerscan.com", "pt-BR"),
        SingleLang("Neox Scanlator", "https://neoxscans.com", "pt-BR", overrideVersionCode = 6),
        SingleLang("Night Comic", "https://www.nightcomic.com", "en", overrideVersionCode = 1),
        SingleLang("Niji Translations", "https://niji-translations.com", "ar"),
        SingleLang("Ninjavi", "https://ninjavi.com", "ar", overrideVersionCode = 1),
        SingleLang("Nitro Scans", "https://nitroscans.com", "en"),
        SingleLang("NovelMic", "https://novelmic.com", "en", overrideVersionCode = 1),
        SingleLang("Oh No Manga", "https://ohnomanga.com", "en", isNsfw = true),
        SingleLang("Off Scan", "https://offscan.top", "pt-BR", overrideVersionCode = 2),
        SingleLang("OnManga", "https://onmanga.com", "en", overrideVersionCode = 1),
        SingleLang("Origami Orpheans", "https://origami-orpheans.com.br", "pt-BR", overrideVersionCode = 2),
        SingleLang("Paean Scans", "https://paeanscans.com", "en", overrideVersionCode = 1),
        SingleLang("Painful Nightz Scan", "https://painfulnightzscan.com", "en"),
        SingleLang("Platinum Crown", "https://platinumscans.com", "en", overrideVersionCode = 1),
        SingleLang("Pojok Manga", "https://pojokmanga.com", "id", overrideVersionCode = 2),
        SingleLang("PornComix", "https://www.porncomixonline.net", "en", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Pornwha", "https://pornwha.com", "en", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Prisma Scans", "https://prismascans.net", "pt-BR"),
        SingleLang("Projeto Scanlator", "https://projetoscanlator.com", "pt-BR", overrideVersionCode = 2),
        SingleLang("QueensManga ملكات المانجا", "https://queensmanga.com", "ar", className = "QueensManga", overrideVersionCode = 1),
        SingleLang("Random Scan", "https://randomscan.online", "pt-BR", overrideVersionCode = 3),
        SingleLang("Random Translations", "https://randomtranslations.com", "en", overrideVersionCode = 1),
        SingleLang("Raw Mangas", "https://rawmangas.net", "ja", isNsfw = true, overrideVersionCode = 1),
        SingleLang("RawDEX", "https://rawdex.net", "ko", isNsfw = true, overrideVersionCode = 1),
        SingleLang("ReadAdult", "https://readadult.net", "en", isNsfw = true),
        SingleLang("ReadManhua", "https://readmanhua.net", "en", overrideVersionCode = 2),
        SingleLang("Reaper Scans", "https://reaperscans.com", "en"),
        SingleLang("Renascence Scans (Renascans)", "https://new.renascans.com", "en", className = "RenaScans", overrideVersionCode = 1),
        SingleLang("Reset Scans", "https://reset-scans.com", "en", overrideVersionCode = 3),
        SingleLang("Rüya Manga", "https://www.ruyamanga.com", "tr", className = "RuyaManga", overrideVersionCode = 1),
        SingleLang("S2Manga", "https://s2manga.com", "en", overrideVersionCode = 1),
        SingleLang("SISI GELAP", "https://sisigelap.club/", "id", overrideVersionCode = 1),
        SingleLang("SamuraiScan", "https://samuraiscan.com", "es"),
        SingleLang("Sani-Go", "https://sani-go.net", "ar", className = "SaniGo", overrideVersionCode = 1),
        SingleLang("Scans Raw", "https://scansraw.com", "en"),
        SingleLang("Setsu Scans", "https://setsuscans.com", "en", overrideVersionCode = 1),
        SingleLang("Shield Manga", "https://shieldmanga.club", "en", overrideVersionCode = 2),
        SingleLang("Shooting Star Scans", "https://shootingstarscans.xyz", "en", overrideVersionCode = 1),
        SingleLang("ShoujoHearts", "https://shoujohearts.com", "en", overrideVersionCode = 2),
        SingleLang("SiXiang Scans", "http://www.sixiangscans.com", "en", overrideVersionCode = 1),
        SingleLang("Siyahmelek", "https://siyahmelek.net", "tr", isNsfw = true, overrideVersionCode = 2),
        SingleLang("Skymanga", "https://skymanga.co", "en", overrideVersionCode = 1),
        SingleLang("Sleeping Knight Scans", "https://skscans.com", "en", overrideVersionCode = 2),
        SingleLang("Sleepy Translations", "https://sleepytranslations.com/", "en", overrideVersionCode = 1),
        SingleLang("Solo Leveling", "https://readsololeveling.online", "en"),
        SingleLang("StageComics", "https://stagecomics.com", "pt-BR", overrideVersionCode = 2),
        SingleLang("Sugar Babies", "https://sugarbscan.com", "en", overrideVersionCode = 1),
        SingleLang("Sweet Time Scan", "https://sweetscan.net", "pt-BR", overrideVersionCode = 1),
        SingleLang("TheFluffyHangoutGroup", "https://www.fluffyhangout.club", "en", overrideVersionCode = 2),
        SingleLang("Three Queens Scanlator", "https://tqscan.com.br", "pt-BR", overrideVersionCode = 2),
        SingleLang("Time Naight", "https://timenaight.com", "tr"),
        SingleLang("Todaymic", "https://todaymic.com", "en"),
        SingleLang("ToonGod", "https://www.toongod.com", "en", overrideVersionCode = 1),
        SingleLang("Toonily", "https://toonily.com", "en", isNsfw = true, overrideVersionCode = 2),
        SingleLang("Toonily.net", "https://toonily.net", "en", isNsfw = true, className = "ToonilyNet", overrideVersionCode = 1),
        SingleLang("Top Manhua", "https://topmanhua.com", "en", overrideVersionCode = 1),
        SingleLang("Traducciones Amistosas", "https://nartag.com", "es", overrideVersionCode = 1),
        SingleLang("TritiniaScans", "https://tritinia.com", "en", overrideVersionCode = 1),
        SingleLang("TruyenTranhAudio.com", "https://truyentranhaudio.com", "vi", className = "TruyenTranhAudioCom"),
        SingleLang("TruyenTranhAudio.online", "https://truyentranhaudio.online", "vi", className = "TruyenTranhAudioOnline"),
        SingleLang("Tsundoku Traduções", "https://tsundokutraducoes.com.br", "pt-BR", pkgName = "tsundokutraducoes", className = "TsundokuTraducoes", overrideVersionCode = 2),
        SingleLang("TuManga.net", "https://tumanga.net", "es", className = "TuMangaNet"),
        SingleLang("Twilight Scans", "https://twilightscans.com", "en", overrideVersionCode = 1),
        SingleLang("Türkçe Manga", "https://turkcemanga.com", "tr", className = "TurkceManga", overrideVersionCode = 1),
        SingleLang("Unemployed Scans", "https://unemployedscans.com", "en", overrideVersionCode = 1),
        SingleLang("Uyuyan Balik", "https://uyuyanbalik.com/", "tr", overrideVersionCode = 1),
        SingleLang("Vanguard Bun", "https://vanguardbun.com/", "en", overrideVersionCode = 1),
        SingleLang("Vapo Scan", "https://vaposcan.net", "pt-BR"),
        SingleLang("Visbellum", "https://visbellum.com", "pt-BR", overrideVersionCode = 1),
        SingleLang("Volkan Scans", "https://volkanscans.com", "en", overrideVersionCode = 1),
        SingleLang("Wakamics", "https://wakamics.net", "en"),
        SingleLang("Wakascan", "https://wakascan.com", "fr", overrideVersionCode = 1),
        SingleLang("War Queen Scan", "https://wqscan.com.br", "pt-BR", overrideVersionCode = 3),
        SingleLang("WebNovel", "https://webnovel.live", "en", className = "WebNovelLive", overrideVersionCode = 3),
        SingleLang("WebToonily", "https://webtoonily.com", "en"),
        SingleLang("Webtoon Hatti", "https://webtoonhatti.com/", "tr"),
        SingleLang("WebtoonUK", "https://webtoon.uk", "en", overrideVersionCode = 1),
        SingleLang("WebtoonXYZ", "https://www.webtoon.xyz", "en", overrideVersionCode = 2),
        SingleLang("Winter Scan", "https://winterscan.com.br", "pt-BR", overrideVersionCode = 2),
        SingleLang("Wonderland", "https://landwebtoons.site", "pt-BR", overrideVersionCode = 2),
        SingleLang("WoopRead", "https://woopread.com", "en", overrideVersionCode = 1),
        SingleLang("WuxiaWorld", "https://wuxiaworld.site", "en", overrideVersionCode = 1),
        SingleLang("XManga", "https://xmanga.io", "en", isNsfw = true),
        SingleLang("XuN Scans", "https://xunscans.xyz", "en", overrideVersionCode = 2),
        SingleLang("Yaoi Fan Clube", "https://yaoifanclube.com.br", "pt-BR", isNsfw = true, overrideVersionCode = 1),
        SingleLang("Yaoi.mobi", "https://yaoi.mobi", "en", isNsfw = true, className = "YaoiManga", pkgName = "yaoimanga", overrideVersionCode = 4),
        SingleLang("Yaoi Toshokan", "https://yaoitoshokan.net", "pt-BR", isNsfw = true, overrideVersionCode = 2),
        SingleLang("Yuri Verso", "https://yuri.live", "pt-BR", overrideVersionCode = 2),
        SingleLang("Zinmanga", "https://zinmanga.com", "en"),
        SingleLang("Zinmanhwa", "https://zinmanhwa.com", "en"),
        SingleLang("ZuttoManga", "https://zuttomanga.com", "en"),
        SingleLang("شبكة كونان العربية", "https://www.manga.detectiveconanar.com", "ar", className = "DetectiveConanAr", overrideVersionCode = 1),
        SingleLang("مانجا العاشق", "https://3asq.org", "ar", className = "Manga3asq", overrideVersionCode = 1),
        SingleLang("مانجا العرب", "https://www.manhwa.ae", "ar", className = "ManhwaAe"),
        SingleLang("مانجا عرب تيم Manga Arab Team", "https://mangaarabteam.com", "ar", className = "MangaArabTeam"),
        SingleLang("مانجا ليك", "https://mangalek.com", "ar", className = "Mangalek", overrideVersionCode = 1),
        SingleLang("مانجا لينك", "https://mangalink.io", "ar", className = "MangaLinkio", overrideVersionCode = 2),
        SingleLang("موقع لترجمة المانجا", "https://golden-manga.com", "ar", className = "GoldenManga"),
    )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            MadaraGenerator().createAll()
        }
    }
}
