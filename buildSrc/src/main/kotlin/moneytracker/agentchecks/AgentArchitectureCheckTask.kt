package moneytracker.agentchecks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Harness check: enforces project architecture choices that generic Kotlin linters
 * cannot see reliably: Hilt for DI, kotlinx.serialization for JSON, and Room in
 * :core:database for persistence.
 */
abstract class AgentArchitectureCheckTask : DefaultTask() {
    @get:Internal
    abstract val projectRoot: DirectoryProperty

    init {
        group = "verification"
        description = "Checks project-level architecture rules for DI, JSON parsing, and database access."
    }

    @TaskAction
    fun checkArchitecture() {
        val root = projectRoot.get().asFile
        val violations = mutableListOf<String>()
        val settingsText = root.resolve("settings.gradle.kts").readText()
        val rootBuildText = root.resolve("build.gradle.kts").readText()
        val appBuildText = root.resolve("app/build.gradle.kts").readText()
        val databaseBuildFile = root.resolve("core/database/build.gradle.kts")
        val databaseBuildText = databaseBuildFile.takeIf { it.exists() }?.readText().orEmpty()

        fun requireRule(condition: Boolean, message: String) {
            if (!condition) violations += message
        }

        requireRule(
            settingsText.contains("include(\":core:database\")"),
            "Room persistence must live in :core:database; add include(\":core:database\") to settings.gradle.kts."
        )
        requireRule(
            appBuildText.contains("libs.plugins.hilt") &&
                appBuildText.contains("libs.hilt.android") &&
                appBuildText.contains("libs.hilt.android.compiler"),
            "Use Hilt for DI in :app: apply the Hilt plugin and add hilt-android plus hilt compiler through KSP."
        )
        requireRule(
            appBuildText.contains("libs.plugins.kotlin.serialization") &&
                appBuildText.contains("libs.kotlinx.serialization.json"),
            "Use kotlinx.serialization for JSON parsing in :app; do not add Gson, Moshi, or Jackson."
        )
        requireRule(
            databaseBuildText.contains("libs.plugins.room") &&
                databaseBuildText.contains("libs.androidx.room.runtime") &&
                databaseBuildText.contains("libs.androidx.room.compiler"),
            "Use Room in :core:database: apply the Room plugin and add room-runtime plus room-compiler through KSP."
        )
        requireRule(
            rootBuildText.contains("moneytracker.agent-checks"),
            "Root build.gradle.kts must apply the moneytracker.agent-checks plugin."
        )

        val forbiddenPatterns = listOf(
            "Gson JSON parser" to Regex("""\b(com\.google\.code\.gson|gson|Gson)\b"""),
            "Moshi JSON parser" to Regex("""\b(com\.squareup\.moshi|moshi|Moshi)\b"""),
            "Jackson JSON parser" to Regex("""\b(com\.fasterxml\.jackson|jackson|ObjectMapper)\b"""),
            "raw SQLite API" to Regex("""\b(android\.database\.sqlite|SQLiteOpenHelper|SQLiteDatabase)\b"""),
            "Koin DI" to Regex("""\b(io\.insert-koin|org\.koin|koin)\b"""),
        )

        root.walkTopDown()
            .filter {
                it.isFile &&
                    it.extension in setOf("kt", "kts", "toml") &&
                    !it.hasIgnoredDirectorySegment() &&
                    it != root.resolve("buildSrc/src/main/kotlin/moneytracker/agentchecks/AgentArchitectureCheckTask.kt")
            }
            .forEach { file ->
                val text = file.readText()
                forbiddenPatterns.forEach { (name, pattern) ->
                    if (pattern.containsMatchIn(text)) {
                        violations += "${file.relativeTo(root).invariantSeparatorsPath}: forbidden $name. Use Hilt, kotlinx.serialization, and Room."
                    }
                }
            }

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("agentArchitectureCheck failed:")
                    violations.forEach { appendLine("- $it") }
                }
            )
        }
    }
}
