import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import net.sghill.moderne.GroupsFactory
import net.sghill.moderne.PluginGroupSpec
import net.sghill.moderne.UpdateCenter
import net.sghill.moderne.UpdateCenterRepositoryLookup
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import kotlin.io.path.outputStream

@OptIn(ExperimentalSerializationApi::class)
suspend fun main() {
    val plugins = Files.readAllLines(Paths.get(System.getenv("PLUGINS_INPUT_FILE"))).map { it.trim() }.filterNot { it.isBlank() }.toSet()
    if (plugins.isEmpty()) {
        throw IllegalStateException("No plugins found. Please define PLUGINS_INPUT_FILE to a file with one plugin id per line")
    }
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }.use { client ->
        val updateCenter: UpdateCenter = client.get("https://updates.jenkins.io/current/update-center.actual.json").body()
        val factory = GroupsFactory(UpdateCenterRepositoryLookup(updateCenter))
        val result = factory.create(PluginGroupSpec("my-plugins", plugins, "plugins from work"))

        if (result.missing.isNotEmpty()) {
            println("The following plugin ids are not in the update center:")
            for (plugin in result.missing.map { it.id }.sorted()) {
                println("\t${plugin}")
            }
        }

        val json = Json { encodeDefaults = true }
        val output = Paths.get("out.json")
        if (Files.notExists(output)) {
            Files.createFile(output)
        }
        output.outputStream(CREATE, TRUNCATE_EXISTING).use {
            json.encodeToStream(result.pluginGroup, it)
        }
    }
}
