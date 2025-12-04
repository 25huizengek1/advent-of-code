plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(25)
    compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
}
