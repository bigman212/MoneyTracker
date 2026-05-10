plugins {
    id("moneytracker.android.library")
}

android {
    namespace = "com.moneytracker.core.domain"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
