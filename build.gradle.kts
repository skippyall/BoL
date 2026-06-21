@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = providers.gradleProperty("project_group").get()
version = providers.gradleProperty("project_version").get()

repositories {
    mavenCentral()
}

kotlin {
    js {
        browser {
        }
        binaries.executable()
    }
    jvm {
        mainRun {
            mainClass = "io.github.skippyall.bol.BoLKt"
        }
        binaries {
            executable {
                mainClass = "io.github.skippyall.bol.BoLKt"
            }
        }
    }

    sourceSets {
        jsMain.dependencies {

        }

        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${providers.gradleProperty("kotlin_serialization_version").get()}")
        }

        jvmMain.dependencies {
            implementation("io.javalin:javalin:${providers.gradleProperty("javalin_version").get()}")
            implementation("org.slf4j:slf4j-simple:${providers.gradleProperty("slf4j_version").get()}")
        }
    }
    ext {
        jvmToolchain(21)
    }
}

tasks.withType<KotlinJsCompile>().configureEach {
    compilerOptions {
        target = "es2015"
    }
}

tasks.named<ProcessResources>("jvmProcessResources") {
    val taskName = if (project.hasProperty("isProduction")
        || project.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.named<KotlinWebpack>(taskName)
    dependsOn(webpackTask)
    from(webpackTask.map {
        println(it.mainOutputFile)
        it.mainOutputFile.get().asFile
    }) {
        into("static")
    } // bring output file along into the JAR
}