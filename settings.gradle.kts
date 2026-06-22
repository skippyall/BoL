pluginManagement {
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
        kotlin("multiplatform") version providers.gradleProperty("kotlin_multiplatform_plugin_version")
        kotlin("plugin.serialization") version providers.gradleProperty("kotlin_serialization_plugin_version")
        id("com.gradleup.shadow") version providers.gradleProperty("shadow_plugin_version")
    }
}

rootProject.name = "BoL"