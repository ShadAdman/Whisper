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

kotlin {
    jvmToolchain(17)
    applyDefaultHierarchyTemplate()
    
    androidTarget()
    
    jvm("desktop")

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
