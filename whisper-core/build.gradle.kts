plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.whisper.core"
    compileSdk = 34
    defaultConfig {
        minSdk = 29
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

val publishMode = providers.gradleProperty("whisper.publishMode").getOrElse("kmp")

kotlin {
    applyDefaultHierarchyTemplate()
    
    androidTarget()
    
    val jvmTargetName = if (publishMode == "jvm") "jvm" else "desktop"
    jvm(jvmTargetName) {
        compilations.all {
            kotlinSourceSets.forEach { it.kotlin.srcDir("src/jvmMain/kotlin") }
        }
    }

    linuxX64()
    macosX64()
    macosArm64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
//    mingwX64()

    sourceSets {
        commonMain.dependencies {
            // No dependencies
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
