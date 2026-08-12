import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatform)
    alias(libs.plugins.vanniktechPublish)
}

kotlin {
    applyDefaultHierarchyTemplate()
    
    val xcf = XCFramework("Whisper")

    androidLibrary {
        namespace = "com.whisper"
        compileSdk = 34
        minSdk = 29
    }
    
    jvm("desktop")

    linuxX64()
    
    macosX64 {
        binaries.framework {
            baseName = "Whisper"
            xcf.add(this)
            linkerOpts("-L${project(":whisper-dsp").projectDir}/prebuilt/desktop/macos/lib", "-lliquid")
        }
    }
    macosArm64 {
        binaries.framework {
            baseName = "Whisper"
            xcf.add(this)
            linkerOpts("-L${project(":whisper-dsp").projectDir}/prebuilt/desktop/macos/lib", "-lliquid")
        }
    }

    iosX64 {
        binaries.framework {
            baseName = "Whisper"
            xcf.add(this)
            linkerOpts("-L${project(":whisper-dsp").projectDir}/prebuilt/ios/lib", "-lliquid")
        }
    }
    iosArm64 {
        binaries.framework {
            baseName = "Whisper"
            xcf.add(this)
            linkerOpts("-L${project(":whisper-dsp").projectDir}/prebuilt/ios/lib", "-lliquid")
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "Whisper"
            xcf.add(this)
            linkerOpts("-L${project(":whisper-dsp").projectDir}/prebuilt/ios/lib", "-lliquid")
        }
    }
    
//    mingwX64()

    sourceSets {
        commonMain.dependencies {
            api(project(":whisper-core"))
            api(project(":whisper-crypto"))
            api(project(":whisper-dsp"))
            api(project(":whisper-audio"))
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
