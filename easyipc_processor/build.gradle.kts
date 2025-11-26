plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
}

dependencies {
    testImplementation(libs.junit)

    api(project(":easyipc_annotations"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.2.10-2.0.2")
    implementation(libs.kotlinpoet.ksp)
    implementation(libs.koin.core)
}