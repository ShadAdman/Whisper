plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.whisper.crypto"
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

    linuxX64 {
        compilations.getByName("main") {
            cinterops {
                val libcrypto by creating
            }
        }
        binaries {
            all {
                linkerOpts("-L/usr/lib/x86_64-linux-gnu", "-lcrypto")
            }
        }
    }
    macosX64()
    macosArm64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
