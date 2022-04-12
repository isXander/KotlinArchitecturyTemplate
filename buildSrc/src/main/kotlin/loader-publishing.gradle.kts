plugins {
    id("dev.architectury.loom")
    id("com.modrinth.minotaur")
    id("com.matthewprenger.cursegradle")
}

val minecraftVersion: String by rootProject

modrinth {
    val modrinthId: String by rootProject
    token.set(findProperty("modrinth.token")?.toString())
    projectId.set(modrinthId)
    versionName.set("[${project.name.capitalize()} $minecraftVersion] ${project.version}")
    versionNumber.set("${project.version}-${project.name}")
    versionType.set("release")
    uploadFile.set(tasks.remapJar.get())
    gameVersions.set(listOf(minecraftVersion))
    loaders.set(listOf(project.name))
    changelog.set(extra["changelog"].toString())
    dependencies.add(com.modrinth.minotaur.dependencies.Dependency("modmenu", "optional"))
}

rootProject.tasks["publishToModrinth"].dependsOn(tasks["modrinth"])

val curseforgeId: String by rootProject
if (hasProperty("curseforge.token") && curseforgeId != "unset") {
    curseforge {
        apiKey = findProperty("curseforge.token")
        project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
            mainArtifact(tasks.remapJar.get(), closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
                displayName = "[${project.name.capitalize()} $minecraftVersion] ${project.version}"
            })

            id = curseforgeId
            releaseType = if (project.name == "fabric") "release" else "beta"
            addGameVersion(minecraftVersion)
            addGameVersion(project.name.capitalize())
            addGameVersion("Java 17")

            relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
                optionalDependency("modmenu")
            })

            changelog = extra["changelog"]
            changelogType = "markdown"
        })

        options(closureOf<com.matthewprenger.cursegradle.Options> {
            forgeGradleIntegration = false
        })
    }
}

rootProject.tasks["publishToCurseforge"].dependsOn(tasks["curseforge"])
