package moneytracker.agentchecks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Harness check: screen and component container/background colors must be opaque.
 *
 * Translucent Compose container colors are rendered by blending with the parent or
 * window background, so they can look different in Preview, Paparazzi, and runtime.
 */
abstract class ComposeOpaqueContainerColorCheckTask : DefaultTask(), ProjectRootCheckTask {
    @get:Internal
    abstract override val projectRoot: DirectoryProperty

    init {
        group = "verification"
        description = "Checks that Compose container and background colors are opaque."
    }

    @TaskAction
    fun checkOpaqueContainerColors() {
        val root = projectRoot.get().asFile
        val violations = root.walkTopDown()
            .filter { file ->
                file.isFile &&
                    file.extension == "kt" &&
                    !file.hasIgnoredDirectorySegment() &&
                    "/src/main/" in file.invariantSeparatorsPath
            }
            .flatMap { file ->
                val lines = file.readLines()
                lines.mapIndexedNotNull { index, _ ->
                    val expression = lines.drop(index).take(MAX_EXPRESSION_LINES).joinToString(" ")
                    val hasContainerOrBackgroundColor =
                        OPAQUE_REQUIRED_COLOR_ARGUMENTS.any { argument -> "$argument =" in expression } ||
                            ".background(" in expression
                    val hasAlphaCopy = ".copy(alpha" in expression

                    if (hasContainerOrBackgroundColor && hasAlphaCopy) {
                        "${file.relativeTo(root).invariantSeparatorsPath}:${index + 1}: avoid translucent container/background colors"
                    } else {
                        null
                    }
                }
            }
            .toList()
            .distinct()

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("composeOpaqueContainerColorCheck failed:")
                    appendLine(
                        "Use opaque colors for Compose container/background surfaces. " +
                            "Alpha blending can render differently in Preview, Paparazzi, and runtime."
                    )
                    violations.forEach { appendLine("- $it") }
                }
            )
        }
    }

    companion object {
        private const val MAX_EXPRESSION_LINES = 4

        private val OPAQUE_REQUIRED_COLOR_ARGUMENTS = listOf(
            "containerColor",
            "backgroundColor"
        )
    }
}
