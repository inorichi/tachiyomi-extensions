package eu.kanade.tachiyomi.extension.all.mangadventure

import eu.kanade.tachiyomi.source.SourceFactory

class MangAdventureFactory : SourceFactory {
    override fun createSources() = listOf(
        ArcRelight(),
        DecadenceScans()
    )

    /** Arc-Relight source. */
    class ArcRelight : MangAdventure(
        "Arc-Relight", "https://arc-relight.com", arrayOf(
            "4-Koma",
            "Chaos;Head",
            "Collection",
            "Comedy",
            "Drama",
            "Jubilee",
            "Mystery",
            "Psychological",
            "Robotics;Notes",
            "Romance",
            "Sci-Fi",
            "Seinen",
            "Shounen",
            "Steins;Gate",
            "Supernatural",
            "Tragedy"
        )
    )

    /** Decadence Scans source. */
    class DecadenceScans : MangAdventure(
        "Decadence Scans", "https://reader.decadencescans.com"
    )
}
