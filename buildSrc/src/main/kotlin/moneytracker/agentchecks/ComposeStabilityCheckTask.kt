package moneytracker.agentchecks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Harness check: scans Compose compiler reports for unstable composable
 * parameters. If it fails, inspect the reported type; if it is genuinely
 * immutable/stable, add it to config/compose/stability.conf with a comment.
 */
abstract class ComposeStabilityCheckTask : DefaultTask(), ProjectRootCheckTask {
    @get:Internal
    abstract override val projectRoot: DirectoryProperty

    init {
        group = "verification"
        description = "Checks Compose compiler reports for unstable composable parameters."
    }

    @TaskAction
    fun checkComposeStability() {
        val root = projectRoot.get().asFile
        val reportFiles = root.walkTopDown()
            .filter { file ->
                file.isFile &&
                    !file.invariantSeparatorsPath.contains("/buildSrc/") &&
                    file.invariantSeparatorsPath.contains("/build/reports/compose/compiler/") &&
                    file.name.endsWith("-composables.txt")
            }
            .toList()

        if (reportFiles.isEmpty()) {
            throw GradleException(
                "composeStabilityCheck failed: no Compose compiler composables reports found. Run ./gradlew compileDebugKotlin or keep composeCompiler.reportsDestination configured."
            )
        }

        val unstableLines = reportFiles.flatMap { file ->
            file.readLines()
                .mapIndexedNotNull { index, line ->
                    val trimmed = line.trim()
                    val isUnstableParameter = trimmed.startsWith("unstable ") || trimmed.contains(" unstable ")
                    if (isUnstableParameter) {
                        "${file.relativeTo(root).invariantSeparatorsPath}:${index + 1}: $trimmed"
                    } else {
                        null
                    }
                }
        }

        if (unstableLines.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("composeStabilityCheck failed: unstable Compose parameters found.")
                    appendLine("If the type is truly immutable/stable, document it in config/compose/stability.conf.")
                    unstableLines.forEach { appendLine("- $it") }
                }
            )
        }
    }
}
