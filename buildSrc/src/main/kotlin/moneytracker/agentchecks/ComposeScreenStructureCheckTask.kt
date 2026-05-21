package moneytracker.agentchecks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Harness check: every public Compose screen function must have a private
 * content function with the same prefix, for example MainScreen -> MainScreenContent.
 * Public screen and UI component composables must also have a matching preview.
 */
abstract class ComposeScreenStructureCheckTask : DefaultTask(), ProjectRootCheckTask {
    @get:Internal
    abstract override val projectRoot: DirectoryProperty

    init {
        group = "verification"
        description = "Checks Compose screen structure and required previews."
    }

    @TaskAction
    fun checkComposeScreenStructure() {
        val root = projectRoot.get().asFile
        val violations = root.walkTopDown()
            .filter { file ->
                file.isFile &&
                    file.extension == "kt" &&
                    !file.hasIgnoredDirectorySegment() &&
                    "/src/main/" in file.invariantSeparatorsPath
            }
            .flatMap { file ->
                val text = file.readText()
                val screenViolations = findPublicScreenNames(text)
                    .filterNot { screenName ->
                        text.contains("private fun ${screenName}Content(")
                    }
                    .map { screenName ->
                        "${file.relativeTo(root).invariantSeparatorsPath}: public screen $screenName must have private ${screenName}Content"
                    }
                val previewViolations = findPublicPreviewRequiredComposableNames(text)
                    .filterNot { composableName ->
                        hasPreviewForComposable(text, composableName)
                    }
                    .map { composableName ->
                        "${file.relativeTo(root).invariantSeparatorsPath}: composable $composableName must have a matching @Preview"
                    }

                screenViolations + previewViolations
            }
            .toList()

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("composeScreenStructureCheck failed:")
                    violations.forEach { appendLine("- $it") }
                }
            )
        }
    }

    private fun findPublicScreenNames(text: String): List<String> {
        val functionRegex = Regex("""(?m)^(?!private\s)fun\s+([A-Z][A-Za-z0-9]*Screen)\s*\(""")
        return functionRegex.findAll(text)
            .map { match -> match.groupValues[1] }
            .filter { screenName ->
                val beforeFunction = text.substring(0, text.indexOf("fun $screenName"))
                beforeFunction.takeLast(120).contains("@Composable")
            }
            .toList()
    }

    private fun findPublicPreviewRequiredComposableNames(text: String): List<String> {
        val functionRegex = Regex("""(?m)^(?!private\s)fun\s+([A-Z][A-Za-z0-9]*(?:Screen|Row|Card|Item|State|Content))\s*\(""")
        return functionRegex.findAll(text)
            .map { match -> match.groupValues[1] }
            .filter { composableName ->
                val beforeFunction = text.substring(0, text.indexOf("fun $composableName"))
                beforeFunction.takeLast(120).contains("@Composable")
            }
            .toList()
    }

    private fun hasPreviewForComposable(text: String, composableName: String): Boolean {
        val previewFunctionRegex = Regex("""(?s)@Preview[^\n]*(?:\n@[^\n]*)*\nfun\s+\w*${Regex.escape(composableName)}\w*Preview\s*\(""")
        val directPreviewRegex = Regex("""(?s)@Preview[^\n]*(?:\n@[^\n]*)*\nfun\s+${Regex.escape(composableName)}\s*\(""")
        return previewFunctionRegex.containsMatchIn(text) || directPreviewRegex.containsMatchIn(text)
    }
}
