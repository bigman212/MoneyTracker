package moneytracker.agentchecks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Harness check: every class with a companion object must place it as the first
 * declaration in the class body. This is project convention, not Kotlin default style.
 */
abstract class CompanionObjectTopCheckTask : DefaultTask(), ProjectRootCheckTask {
    @get:Internal
    abstract override val projectRoot: DirectoryProperty

    init {
        group = "verification"
        description = "Checks that companion object is the first declaration inside each class."
    }

    @TaskAction
    fun checkCompanionObjectOrder() {
        val root = projectRoot.get().asFile
        val violations = mutableListOf<String>()
        val classRegex = Regex("""\bclass\s+([A-Za-z_][A-Za-z0-9_]*)""")

        root.walkTopDown()
            .filter {
                it.isFile &&
                    it.extension == "kt" &&
                    !it.hasIgnoredDirectorySegment()
            }
            .forEach { file ->
                val text = file.readText()
                classRegex.findAll(text).forEach classMatches@{ match ->
                    val className = match.groupValues[1]
                    val openBraceIndex = text.indexOf('{', match.range.last)
                    if (openBraceIndex == -1) return@classMatches

                    val closeBraceIndex = findMatchingBrace(text, openBraceIndex)
                    if (closeBraceIndex == -1) return@classMatches

                    val body = text.substring(openBraceIndex + 1, closeBraceIndex)
                    val companionIndex = topLevelCompanionIndex(body)
                    if (companionIndex == -1) return@classMatches

                    val firstDeclarationIndex = skipIgnorableClassBodyPrefix(body)
                    if (firstDeclarationIndex != companionIndex) {
                        violations += "${file.relativeTo(root).invariantSeparatorsPath}: class $className must place companion object before properties, constructors, and functions."
                    }
                }
            }

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("companionObjectTopCheck failed:")
                    violations.forEach { appendLine("- $it") }
                }
            )
        }
    }

    private fun findMatchingBrace(text: String, openBraceIndex: Int): Int {
        var depth = 0
        for (index in openBraceIndex until text.length) {
            when (text[index]) {
                '{' -> depth++
                '}' -> {
                    depth--
                    if (depth == 0) return index
                }
            }
        }
        return -1
    }

    private fun skipIgnorableClassBodyPrefix(body: String): Int {
        var index = 0
        while (index < body.length) {
            while (index < body.length && body[index].isWhitespace()) {
                index++
            }

            if (body.startsWith("//", index)) {
                index = body.indexOf('\n', index).let { if (it == -1) body.length else it + 1 }
                continue
            }

            if (body.startsWith("/*", index)) {
                index = body.indexOf("*/", index + 2).let { if (it == -1) body.length else it + 2 }
                continue
            }

            if (index < body.length && body[index] == '@') {
                var parenthesesDepth = 0
                do {
                    when (body[index]) {
                        '(' -> parenthesesDepth++
                        ')' -> parenthesesDepth--
                    }
                    index++
                } while (
                    index < body.length &&
                        (parenthesesDepth > 0 || body[index - 1] != '\n')
                )
                continue
            }

            return index
        }
        return body.length
    }

    private fun topLevelCompanionIndex(body: String): Int {
        var depth = 0
        var index = 0
        while (index < body.length) {
            if (depth == 0 && body.startsWith("companion object", index)) {
                return index
            }
            when (body[index]) {
                '{' -> depth++
                '}' -> depth--
            }
            index++
        }
        return -1
    }
}
