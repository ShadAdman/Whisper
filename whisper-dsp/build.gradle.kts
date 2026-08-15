import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
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
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_linux.def"))
                includeDirs(file("prebuilt/desktop/linux/include"))
            }
        }
        binaries {
            sharedLib {
                baseName = "WhisperDSP"
            }
            all {
                linkerOpts("-L${project.file("prebuilt/desktop/linux/lib").absolutePath}", "-lliquid")
            }
        }
    }

    macosX64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_macos.def"))
                includeDirs(file("prebuilt/desktop/macos/include"))
            }
        }
        binaries {
            framework {
                baseName = "WhisperDSP"
            }
            all {
                linkerOpts("-L${project.file("prebuilt/desktop/macos/lib").absolutePath}", "-lliquid")
            }
        }
    }

    macosArm64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_macos.def"))
                includeDirs(file("prebuilt/desktop/macos/include"))
            }
        }
        binaries {
            framework {
                baseName = "WhisperDSP"
            }
            all {
                linkerOpts("-L${project.file("prebuilt/desktop/macos/lib").absolutePath}", "-lliquid")
            }
        }
    }

    iosX64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_ios.def"))
                includeDirs(file("prebuilt/ios-simulator/include"))
            }
        }
        binaries {
            framework {
                baseName = "WhisperDSP"
            }
            all {
                linkerOpts("-L${project.file("prebuilt/ios-simulator/lib").absolutePath}", "-lliquid")
            }
        }
    }
    iosArm64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_ios.def"))
                includeDirs(file("prebuilt/ios/include"))
            }
        }
        binaries {
            framework {
                baseName = "WhisperDSP"
            }
            all {
                linkerOpts("-L${project.file("prebuilt/ios/lib").absolutePath}", "-lliquid")
            }
        }
    }
    iosSimulatorArm64 {
        compilations.getByName("main") {
            cinterops.create("liquid") {
                definitionFile.set(project.file("src/nativeInterop/cinterop/liquid_ios.def"))
                includeDirs(file("prebuilt/ios-simulator/include"))
            }
        }
        binaries {
            framework {
                baseName = "WhisperDSP"
            }
            all {
                linkerOpts("-L${project.file("prebuilt/ios-simulator/lib").absolutePath}", "-lliquid")
            }
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

    val jvmSourceSet = if (publishMode == "jvm") "jvmMain" else "desktopMain"
    sourceSets.getByName(jvmSourceSet).dependencies {
        implementation(libs.jna)
    }
}
