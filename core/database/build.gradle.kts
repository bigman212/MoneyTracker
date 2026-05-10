plugins {
    id("moneytracker.android.library")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "com.moneytracker.core.database"
}

room {
    schemaDirectory("$projectDir/schemas")
}

hilt {
    enableAggregatingTask = false
}

dependencies {
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.hilt.android)
    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.android.compiler)
}
