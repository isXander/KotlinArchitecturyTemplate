plugins {
    id("net.kyori.blossom") version "1.+"
}

dependencies {
    val fabricLoaderVersion: String by rootProject
    val clothVersion: String by rootProject

    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    /* mixin extras!
    implementation("com.github.llamalad7:mixinextras:0.0.+")
    annotationProcessor("com.github.llamalad7:mixinextras:0.0.+")
     */
}

architectury {
    common()
}

loom {
    //accessWidenerPath.set(file("src/main/resources/examplemod.accesswidener"))
}

blossom {

}