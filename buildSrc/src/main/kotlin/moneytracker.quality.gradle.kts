import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
}

val libs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

configure<SpotlessExtension> {
    kotlin {
        target("src/**/*.kt")
        targetExclude("**/build/**")
        ktlint(libs.findVersion("ktlintCli").get().requiredVersion)
            .editorConfigOverride(
                mapOf(
                    "ktlint_code_style" to "android_studio",
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                    "ktlint_class_signature_rule_force_multiline_when_parameter_count_greater_or_equal_than" to "2",
                    "ktlint_function_signature_body_expression_wrapping" to "always",
                )
            )
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.findVersion("ktlintCli").get().requiredVersion)
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
    version.set(libs.findVersion("ktlintCli").get().requiredVersion)
    android.set(true)
    verbose.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.SARIF)
    }
    filter {
        exclude("**/build/**")
    }
}

configure<DetektExtension> {
    toolVersion = libs.findVersion("detekt").get().requiredVersion
    buildUponDefaultConfig = true
    allRules = false
    parallel = true
    ignoreFailures = false
    basePath = rootDir.absolutePath
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}

dependencies.add("detektPlugins", project(":lint:detekt-rules"))

tasks.withType<Detekt>().configureEach {
    reports {
        md.required.set(true)
        sarif.required.set(true)
        xml.required.set(false)
        html.required.set(false)
    }
    include("**/*.kt", "**/*.kts")
    exclude("**/build/**")
}
