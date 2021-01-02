package eu.kanade.tachiyomi.multisrc

import java.io.File
import java.util.Locale

/**
 * This is meant to be used in place of a factory extension, specifically for what would be a multi-source extension.
 * A multi-lang (but not multi-source) extension should still be made as a factory extensiion.
 * Use a generator for initial setup of a theme source or when all of the inheritors need a version bump.
 * Source list (val sources) should be kept up to date.
 */
interface ThemeSourceGenerator {
    /**
     * The class that the sources inherit from.
     */
    val themeClass: String

    /**
     * The package that contains themeClass.
     */
    val themePkg: String


    /**
     * Base theme version, starts with 1 and should be increased when based theme class changes
     */
    val baseVersionCode: Int

    /**
     * The list of sources to be created or updated.
     */
    val sources: List<ThemeSourceData>

    fun createAll() {
        val userDir = System.getProperty("user.dir")!!

        sources.forEach { source ->
            createSource(source, themePkg, themeClass, baseVersionCode, userDir)
        }
    }

    companion object {
        private fun pkgNameSuffix(source: ThemeSourceData, separator: String): String {
            return if (source is ThemeSourceData.SingleLang)
                listOf(source.lang.substringBefore("-"), source.pkgName).joinToString(separator)
            else
                listOf("all", source.pkgName).joinToString(separator)
        }

        private fun writeGradle(gradle: File, source: ThemeSourceData, baseVersionCode: Int) {
            gradle.writeText("""apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'

ext {
    extName = '${source.name}'
    pkgNameSuffix = '${pkgNameSuffix(source, ".")}'
    extClass = '.${source.className}'
    extVersionCode = ${baseVersionCode + source.overrideVersionCode + multisrcLibraryVersion}
    libVersion = '1.2'
${if (source.isNsfw) "    containsNsfw = true\n" else ""}}

apply from: "${'$'}rootDir/common.gradle"
"""
            )
        }

        /**
         * Clears directory recursively
         */
        private fun purgeDirectory(dir: File) {
            for (file in dir.listFiles()!!) {
                if (file.isDirectory) purgeDirectory(file)
                file.delete()
            }
        }

        fun createSource(source: ThemeSourceData, themePkg: String, themeClass: String, baseVersionCode: Int, userDir: String) {
            val sourceRootPath = userDir + "/generated-src/${pkgNameSuffix(source, "/")}"
            val srcPath = File("$sourceRootPath/src/eu/kanade/tachiyomi/extension/${pkgNameSuffix(source, "/")}")
            val overridesPath = "$userDir/multisrc/overrides" // userDir = tachiyomi-extensions project root path
            val resOverridePath = "$overridesPath/res/$themePkg"
            val srcOverridePath = "$overridesPath/src/$themePkg"
            val gradleFile = File("$sourceRootPath/build.gradle")
            val androidManifestFile = File("$sourceRootPath/AndroidManifest.xml")


            File(sourceRootPath).let { file ->
                println("Working on $source")

                file.mkdirs()
                purgeDirectory(file)

                writeGradle(gradleFile, source, baseVersionCode)

                srcPath.mkdirs()
                val srcOverride = File("$srcOverridePath/${source.pkgName}")
                if (srcOverride.exists())
                    srcOverride.copyRecursively(File("$srcPath"))
                else
                    writeSourceClass(srcPath, source, themePkg, themeClass)


                // copy res files
                // check if res override exists if not copy default res
                val resOverride = File("$resOverridePath/${source.pkgName}")
                if (resOverride.exists())
                    resOverride.copyRecursively(File("$sourceRootPath/res"))
                else
                    File("$resOverridePath/default").let { res ->
                        if (res.exists()) res.copyRecursively(File("$sourceRootPath/res"))
                    }
            }
        }

        private fun writeSourceClass(classPath: File, source: ThemeSourceData, themePkg: String, themeClass: String) {
            fun factoryClassText(): String {
                val sourceListString =
                    (source as ThemeSourceData.MultiLang).lang.map {
                        "        $themeClass(\"${source.name}\", \"${source.baseUrl}\", \"$it\"),"
                    }.joinToString("\n")

                return """class ${source.className} : SourceFactory {
    override fun createSources(): List<Source> = listOf(
$sourceListString
    )
}"""
            }
            File("$classPath/${source.className}.kt").writeText(
                """package eu.kanade.tachiyomi.extension.${pkgNameSuffix(source, ".")}
${if (source.isNsfw) "\nimport eu.kanade.tachiyomi.annotations.Nsfw" else ""}
import eu.kanade.tachiyomi.multisrc.$themePkg.$themeClass
${if (source is ThemeSourceData.MultiLang)
                    """import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceFactory
                    """
                else ""}${if (source.isNsfw) "\n@Nsfw" else ""}
${if (source is ThemeSourceData.SingleLang) {
                    "class ${source.className} : $themeClass(\"${source.name}\", \"${source.baseUrl}\", \"${source.lang}\")\n"
                } else
                    factoryClassText()
                }
""")
        }

        sealed class ThemeSourceData {
            abstract val name: String
            abstract val baseUrl: String
            abstract val isNsfw: Boolean
            abstract val className: String
            abstract val pkgName: String

            /**
             * overrideVersionCode defaults to 0, if a source changes their override code or
             * a previous existing source suddenly needs overrides, overrideVersionCode should be increased.
             * When a new source is added with overrides, overrideVersionCode is still the default 0
             */
            abstract val overrideVersionCode: Int

            data class SingleLang(
                override val name: String,
                override val baseUrl: String,
                val lang: String,
                override val isNsfw: Boolean = false,
                override val className: String = name.replace(" ", ""),
                override val pkgName: String = className.toLowerCase(Locale.ENGLISH),
                override val overrideVersionCode: Int = 0,
            ) : ThemeSourceData()

            data class MultiLang(
                override val name: String,
                override val baseUrl: String,
                val lang: List<String>,
                override val isNsfw: Boolean = false,
                override val className: String = name.replace(" ", "") + "Factory",
                override val pkgName: String = className.substringBefore("Factory").toLowerCase(Locale.ENGLISH),
                override val overrideVersionCode: Int = 0,
            ) : ThemeSourceData()
        }
    }
}


/**
 * This variable should be increased when the multisrc library changes in a way that prompts global extension upgrade
 */
const val multisrcLibraryVersion = 0
