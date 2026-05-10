import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.spotless) apply false
}

val agentCheckTaskNames = listOf(
    "spotlessCheck",
    "ktlintCheck",
    "detekt",
    "lintDebug",
    "testDebugUnitTest",
    "assembleDebug",
)

subprojects {
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<SpotlessExtension> {
        kotlin {
            target("src/**/*.kt")
            targetExclude("**/build/**")
            ktlint(rootProject.libs.versions.ktlintCli.get())
                .editorConfigOverride(
                    mapOf(
                        "ktlint_code_style" to "android_studio",
                        "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                    )
                )
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint(rootProject.libs.versions.ktlintCli.get())
        }
        format("misc") {
            target("*.md", ".gitignore", "*.properties")
            targetExclude("**/build/**")
            trimTrailingWhitespace()
            leadingTabsToSpaces(4)
            endWithNewline()
        }
    }

    configure<KtlintExtension> {
        version.set(rootProject.libs.versions.ktlintCli.get())
        android.set(true)
        verbose.set(true)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.SARIF)
        }
        filter {
            exclude("**/build/**")
        }
    }

    configure<DetektExtension> {
        toolVersion = rootProject.libs.versions.detekt.get()
        buildUponDefaultConfig = true
        allRules = false
        parallel = true
        ignoreFailures = false
        basePath = rootDir.absolutePath
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    }

    tasks.withType<Detekt>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
            md.required.set(true)
            sarif.required.set(true)
        }
        include("**/*.kt", "**/*.kts")
        exclude("**/build/**")
    }
}

val agentCheck = tasks.register("agentCheck") {
    group = "verification"
    description = "Runs the standard agent verification checks."
}

gradle.projectsEvaluated {
    val orderedTasks = agentCheckTaskNames.flatMap { taskName ->
        val matchingTasks = allprojects.mapNotNull { project ->
            project.tasks.findByName(taskName)
        }

        if (matchingTasks.isEmpty()) {
            logger.warn("agentCheck: task '$taskName' was not found; skipping it.")
        }

        matchingTasks
    }

    agentCheck.configure {
        dependsOn(orderedTasks)
    }

    orderedTasks.zipWithNext { previousTask, nextTask ->
        nextTask.mustRunAfter(previousTask)
    }
}
