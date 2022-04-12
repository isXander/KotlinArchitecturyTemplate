plugins {
    id("com.github.johnrengelman.shadow") version "7.+"
    `loader-publishing`
}

val modName: String by rootProject
base.archivesName.set("$modName-${project.name}")

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    //accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentFabric"].extendsFrom(this)
}
val shadowCommon by configurations.creating

dependencies {
    val fabricLoaderVersion: String by rootProject

    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }

    /* mixin extras!
    "com.github.llamalad7:mixinextras:0.0.+".let {
        implementation(it)
        annotationProcessor(it)
        include(it)
    }
     */

    modImplementation("com.terraformersmc:modmenu:3.+")
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
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

afterEvaluate {
    loom.decompilerOptions.asMap
        .forEach { (name, options) -> logger.lifecycle("decompiler: $name: ${options::class.java.name}") }

}
