package net.sghill.moderne

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class PluginGroupSpec(
    val name: String,
    val pluginIds: Set<String>,
    val description: String = "",
)

data class PluginGroupResult(val pluginGroup: PluginGroup, val missing: Set<PluginResult.Missing>)

@Serializable
data class PluginGroup(
    val name: String,
    val description: String,
    val repositories: Set<GitHubRepository>,
    val count: Int = repositories.size,
)

@Serializable
data class GitHubRepository @OptIn(ExperimentalSerializationApi::class) constructor(
    val branch: String,
    val path: String,
    @EncodeDefault
    @SerialName("__typename")
    val typeName: String = GitHubRepository::class.java.simpleName,
    @EncodeDefault
    val origin: String = "github.com",
)

interface RepositoryLookup {
    fun byId(id: String): PluginResult
}

class UpdateCenterRepositoryLookup(private val updateCenter: UpdateCenter) : RepositoryLookup {
    override fun byId(id: String): PluginResult {
        val plugin = updateCenter.plugins[id] ?: return PluginResult.Missing(id)
        val branch = plugin.defaultBranch
        val scm = plugin.scm
        return if (branch == null || scm == null) {
            PluginResult.Missing(id)
        } else {
            val path = scm.substringAfter("https://github.com/")
            PluginResult.Ok(
                GitHubRepository(
                    branch,
                    path,
                    path.substringAfterLast("/"),
                    path.substringBeforeLast("/")
                )
            )
        }
    }
}

sealed interface PluginResult {
    data class Ok(val repository: GitHubRepository) : PluginResult
    data class Missing(val id: String) : PluginResult
}

class GroupsFactory(private val lookup: RepositoryLookup) {
    fun create(spec: PluginGroupSpec): PluginGroupResult {
        val results = spec.pluginIds.map { lookup.byId(it) }
        val missing = results.filterIsInstance<PluginResult.Missing>().toSet()
        val repositories = results.filterIsInstance<PluginResult.Ok>().map { it.repository }.toSet()
        val group = PluginGroup(spec.name, spec.description, repositories)
        return PluginGroupResult(group, missing)
    }
}

@Serializable
data class UpdateCenterPlugin(
    val defaultBranch: String? = null,
    val scm: String? = null,
    val releaseTimestamp: String,
    val requiredCore: String,
    val size: Int
)

@Serializable
data class UpdateCenter(val plugins: Map<String, UpdateCenterPlugin>)
