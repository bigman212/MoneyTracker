plugins {
    id("moneytracker.android.library")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.moneytracker.core.data"
}

hilt {
    enableAggregatingTask = false
}

dependencies {
    implementation(project(":core:database"))
    api(project(":core:domain"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
