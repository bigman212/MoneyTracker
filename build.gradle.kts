// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("moneytracker.agent-checks")
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}

subprojects {
    if (buildFile.exists() && path != ":lint:detekt-rules") {
        apply(plugin = "moneytracker.quality")
    }
}
