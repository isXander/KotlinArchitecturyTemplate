plugins {
    id("com.github.johnrengelman.shadow") version "7.+"
    `loader-publishing`
}

val modName: String by rootProject
base.archivesName.set("$modName-${project.name}")

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    //accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge.apply {
        mixinConfig("$modName-common.mixins.json")
        mixinConfig("$modName.mixins.json")
        convertAccessWideners.set(true)
        //extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

val common by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentForge"].extendsFrom(this)
}
val shadowCommon by configurations.creating

dependencies {
    val minecraftVersion: String by rootProject
    val forgeVersion: String by rootProject

    forge("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionForge")) { isTransitive = false }

    /* mixin extras!
    "com.github.llamalad7:mixinextras:0.0.+".let {
        forgeRuntimeLibrary(it)
        implementation(it)
        annotationProcessor(it)
        include(it)
    }
     */
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("META-INF/mods.toml") {
            expand(
                "version" to project.version
            )
        }
    }

    shadowJar {
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
        archiveClassifier.set(null as String?)
    }

    jar {
        archiveClassifier.set("dev")
    }
}

components["java"].withGroovyBuilder {
    "withVariantsFromConfiguration"(configurations["shadowRuntimeElements"]) {
        "skip"()
    }
}

//quiltflower {
//    addToRuntimeClasspath.set(true)
//}
