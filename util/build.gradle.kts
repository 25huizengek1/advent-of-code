plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(25)
    compilerOptions.freeCompilerArgs.addAll("-Xcontext-parameters", "-Xnested-type-aliases")
}
