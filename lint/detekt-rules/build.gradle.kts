plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    compileOnly(libs.detekt.api)

    testImplementation(libs.junit)
    testImplementation(libs.konsist)
}
