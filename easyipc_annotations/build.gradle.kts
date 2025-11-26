plugins {
    kotlin("jvm")
}

apply(from = "${rootProject.rootDir}/publish.gradle.kts")

dependencies {
    testImplementation(libs.junit)
}