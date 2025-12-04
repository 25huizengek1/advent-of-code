plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.logback)

    implementation(projects.archive)
    implementation(projects.current)
    implementation(projects.util)
}

kotlin {
    jvmToolchain(25)
    compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
}

application {
    mainClass = "nl.bartoostveen.aoc.runner.RunnerKt"
}
