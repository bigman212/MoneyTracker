package moneytracker.agentchecks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Harness check for formatting decisions that are intentionally stricter than
 * the generic formatter defaults.
 */
abstract class FormattingConventionsCheckTask : DefaultTask(), ProjectRootCheckTask {
    @get:Internal
    abstract override val projectRoot: DirectoryProperty

    init {
        group = "verification"
        description = "Checks project-specific Kotlin formatting conventions."
    }

    @TaskAction
    fun checkFormattingConventions() {
        val root = projectRoot.get().asFile
        val violations = root.walkTopDown()
            .filter { file ->
                file.isFile &&
                    file.extension == "kt" &&
                    !file.hasIgnoredDirectorySegment()
            }
            .flatMap { file ->
                val relativePath = file.relativeTo(root).invariantSeparatorsPath
                val text = file.readText()
                Regex("""data\s+class\s+\w+\(([^)\n]*\bval\b[^)\n]*,\s*[^)\n]*\bval\b[^)\n]*)\)""")
                    .findAll(text)
                    .map { match ->
                        "$relativePath: data classes with multiple properties must place each property on its own line: ${match.value}"
                    }
            }
            .toList()

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("formattingConventionsCheck failed:")
                    violations.forEach { appendLine("- $it") }
                }
            )
        }
    }
}
