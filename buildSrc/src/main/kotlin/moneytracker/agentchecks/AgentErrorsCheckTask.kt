package moneytracker.agentchecks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Harness check: reads agents.error.md and fails on unresolved agent-process
 * mistakes. Lines marked with `(fixed)` are treated as historical notes.
 */
abstract class AgentErrorsCheckTask : DefaultTask() {
    @get:Internal
    abstract val projectRoot: DirectoryProperty

    init {
        group = "verification"
        description = "Checks that agents.error.md has no unresolved entries."
    }

    @TaskAction
    fun checkAgentErrors() {
        val errorFile = projectRoot.get().asFile.resolve("agents.error.md")
        if (!errorFile.exists()) return

        val unresolved = errorFile.readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filterNot { it.endsWith("(fixed)") }

        if (unresolved.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("agentErrorsCheck failed: resolve or mark entries in agents.error.md as (fixed):")
                    unresolved.forEach { appendLine("- $it") }
                }
            )
        }
    }
}
