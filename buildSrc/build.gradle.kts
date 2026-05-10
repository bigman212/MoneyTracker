plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.13.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    implementation("com.diffplug.spotless:com.diffplug.spotless.gradle.plugin:8.4.0")
    implementation("io.gitlab.arturbosch.detekt:io.gitlab.arturbosch.detekt.gradle.plugin:1.23.8")
    implementation("org.jlleitschuh.gradle.ktlint:org.jlleitschuh.gradle.ktlint.gradle.plugin:14.2.0")
}

gradlePlugin {
    plugins {
        create("agentChecks") {
            id = "moneytracker.agent-checks"
            implementationClass = "moneytracker.agentchecks.MoneyTrackerAgentChecksPlugin"
        }
    }
}
