plugins {
    id("dev.architectury.loom")
//    id("io.github.juuxel.loom-quiltflower")
    id("org.quiltmc.quilt-mappings-on-loom")
}

dependencies {
    val minecraftVersion: String by rootProject
    val mappingsType: String by rootProject

    minecraft("com.mojang:minecraft:$minecraftVersion")

    when (mappingsType) {
        "quilt+mojmap" ->
            mappings(loom.layered {
                quiltMappings.mappings("org.quiltmc:quilt-mappings:$minecraftVersion+build.+:v2")
                officialMojangMappings()
            })
        "mojmap" ->
            mappings(loom.officialMojangMappings())
        "yarn" ->
            mappings("net.fabricmc:yarn:$minecraftVersion+build.+:v2")
    }
}
