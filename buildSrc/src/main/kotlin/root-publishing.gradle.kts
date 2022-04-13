import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

plugins {
    id("com.modrinth.minotaur")
    id("com.matthewprenger.cursegradle")
    id("com.github.breadmoirai.github-release")
}

allprojects {
    val changelog by extra { rootProject.file("changelogs/${project.version}.md").takeIf { it.exists() }?.readText() }
}

afterEvaluate {
    tasks {
        val publishToModrinth by registering { group = "mod" }
        val publishToCurseforge by registering { group = "mod" }

        val updateApiVersion by registering {
            group = "mod"
            onlyIf { hasProperty("xander-api.username") && hasProperty("xander-api.password") }

            val gson = Gson()

            val client = HttpClient.newHttpClient()

            val loginRequest = HttpRequest.newBuilder(URI.create("https://api.isxander.dev/login")).apply {
                val json = JsonObject()
                json.addProperty("username", findProperty("xander-api.username")?.toString())
                json.addProperty("password", findProperty("xander-api.password")?.toString())
                POST(HttpRequest.BodyPublishers.ofString(gson.toJson(json)))
                header("Content-Type", "application/json")
            }.build()

            val loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString())
            if (loginResponse.statusCode() != 200) {
                println("FAILED TO LOGIN TO API.ISXANDER.DEV")
                println("STATUS CODE: ${loginResponse.statusCode()}")
                println("RESPONSE: ${loginResponse.body()}")
                return@registering
            }

            val loginResponseJson = gson.fromJson(loginResponse.body(), JsonObject::class.java)
            val jwtToken = loginResponseJson.get("token").asString

            val loaders = listOf("forge", "fabric")
            val minecraftVersion: String by rootProject
            val modName: String by rootProject
            for (loader in loaders) {
                val newVersionRequest = HttpRequest.newBuilder(URI.create("https://api.isxander.dev/updater/new/$modName?loader=$loader&minecraft=$minecraftVersion&version=${project.version}")).apply {
                    GET()
                    header("Authorization", "Bearer $jwtToken")
                }.build()

                val response = client.send(newVersionRequest, HttpResponse.BodyHandlers.ofString())
            }
        }

        modrinth {
            val modrinthId: String by rootProject
            token.set(findProperty("modrinth.token")?.takeIf { modrinthId != "unset" }?.toString())
            projectId.set(modrinthId)
            syncBodyFrom.set(file("README.md").readText())
        }

        githubRelease {
            token(findProperty("github.token")?.toString())

            owner("isXander")
            repo("ExampleMod")
            tagName("${project.version}")
            targetCommitish("1.18")
            body(extra["changelog"].toString())
            releaseAssets(project(":fabric").tasks["remapJar"].outputs.files, project(":forge").tasks["remapJar"].outputs.files)
        }

        register("publishMod") {
            group = "mod"

            dependsOn("clean")

            dependsOn(publishToModrinth)
            dependsOn(":modrinthSyncBody")

            dependsOn(publishToCurseforge)

            dependsOn("githubRelease")

            dependsOn(updateApiVersion)
        }
    }

}

