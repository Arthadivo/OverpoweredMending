enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
    }
}

rootProject.name = "OverpoweredMending"
include("common")
include("fabric")
include("forge")
