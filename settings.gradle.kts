pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://server.bbkr.space/artifactory/libs-release")
        maven("https://maven.quiltmc.org/repository/release")
    }
}

rootProject.name = "ExampleMod"

include("common")
include("fabric")
include("forge")
