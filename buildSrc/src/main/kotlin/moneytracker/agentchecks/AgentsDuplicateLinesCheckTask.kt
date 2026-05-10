package moneytracker.agentchecks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Harness check for accidental repeated rules inside AGENTS.md files.
 */
abstract class AgentsDuplicateLinesCheckTask : DefaultTask(), ProjectRootCheckTask {
    @get:Internal
    abstract override val projectRoot: DirectoryProperty

    init {
        group = "verification"
        description = "Checks AGENTS.md files for duplicate non-blank lines."
    }

    @TaskAction
    fun checkAgentsDuplicateLines() {
        val root = projectRoot.get().asFile
        val violations = root.walkTopDown()
            .filter { file ->
                file.isFile &&
                    file.name == "AGENTS.md" &&
                    !file.hasIgnoredDirectorySegment()
            }
            .flatMap { file ->
                val relativePath = file.relativeTo(root).invariantSeparatorsPath
                file.readLines()
                    .mapIndexed { index, line -> AgentsLine(index = index + 1, text = line.trim()) }
                    .filter { line -> line.text.isNotEmpty() }
                    .groupBy { line -> line.text }
                    .filterValues { lines -> lines.size > 1 }
                    .map { (text, lines) ->
                        val lineNumbers = lines.joinToString { line -> line.index.toString() }
                        "$relativePath: duplicate line at $lineNumbers: $text"
                    }
            }
            .toList()

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("agentsDuplicateLinesCheck failed:")
                    violations.forEach { appendLine("- $it") }
                }
            )
        }
    }
}

private data class AgentsLine(
    val index: Int,
    val text: String,
)
