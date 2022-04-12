



plugins {
    id("architectury-plugin") version "3.4.+"
    `root-publishing`
//    id("dev.architectury.loom") version "0.11.0.+" apply false
//    id("io.github.juuxel.loom-quiltflower") version "1.+" apply false
//    id("org.quiltmc.quilt-mappings-on-loom") version "4.+" apply false


}

architectury {
    val minecraftVersion: String by rootProject
    minecraft = minecraftVersion
}

subprojects {
    apply(plugin = "mc-basic-setup")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")

    group = "dev.isxander"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.shedaniel.me")
        maven("https://maven.terraformersmc.com")
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(17)
        }
    }
}


