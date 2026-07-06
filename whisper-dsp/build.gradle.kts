import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kmmbridge)
    `maven-publish`
}

android {
    namespace = "com.whisper.dsp"
    compileSdk = 34
    defaultConfig {
        minSdk = 29
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    externalNativeBuild {
        cmake {
            path = file("src/androidMain/cpp/CMakeLists.txt")
        }
    }
}

kotlin {
    jvmToolchain(17)
    applyDefaultHierarchyTemplate()
    
    androidTarget()
    
    jvm("desktop")

    linuxX64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_linux.def"))
            }
        }
    }

    macosX64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_macos.def"))
            }
        }
        binaries.framework {
            baseName = "WhisperDSP"
        }
    }

    macosArm64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_macos.def"))
            }
        }
        binaries.framework {
            baseName = "WhisperDSP"
        }
    }

    iosX64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_ios.def"))
            }
        }
        binaries.framework {
            baseName = "WhisperDSP"
        }
    }
    iosArm64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_ios.def"))
            }
        }
        binaries.framework {
            baseName = "WhisperDSP"
        }
    }
    iosSimulatorArm64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_ios.def"))
            }
        }
        binaries.framework {
            baseName = "WhisperDSP"
        }
    }

//    mingwX64 {
//        compilations.getByName("main") {
//            cinterops.create("liquid") {
//                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_win.def"))
//            }
//        }
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":whisper-core"))
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        
        val nativeMain by getting

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }

    kmmbridge {
        frameworkName.set("WhisperDSP")
        mavenPublishArtifacts()
        spm()
    }
}
