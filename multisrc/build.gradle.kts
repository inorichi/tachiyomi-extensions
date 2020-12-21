plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(Config.compileSdk)
    buildToolsVersion(Config.buildTools)

    defaultConfig {
        minSdkVersion(Config.minSdk)
        targetSdkVersion(Config.targetSdk)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":annotations"))

    compileOnly("com.github.salomonbrys.kotson:kotson:2.5.0")
    compileOnly(project(":duktape-stub"))
}
apply("$rootDir/common-dependencies.gradle")



tasks.register("runAllGenerators") {
    doLast {
        var classPath = ""
        classPath += configurations.androidApis.get().asFileTree.first().absolutePath + ":" // android.jar path
        classPath += "$projectDir/build/intermediates/aar_main_jar/debug/classes.jar:" // jar made from this module

        configurations.debugCompileOnly.get().asFileTree.forEach { classPath = "$classPath:$it" } // every dependency we have

        val javaPath = System.getProperty("java.home") + "/bin/java" // path of java

        val mainClass = "eu.kanade.tachiyomi.multisrc.GeneratorMain" // Main class we want to execute

        Runtime.getRuntime().exec("$javaPath -classpath $classPath $mainClass") // put it all together
    }
}
