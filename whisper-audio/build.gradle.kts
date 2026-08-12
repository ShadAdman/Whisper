import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.whisper.audio"
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
    applyDefaultHierarchyTemplate()

    androidTarget()
    
    jvm("desktop")

    linuxX64()
    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
        val commonMain by getting {
            dependencies {
                implementation(project(":whisper-core"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
            }
        }
        val appleMain by getting {
            dependsOn(commonMain)
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.startup)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
