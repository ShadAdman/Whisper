import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatform)
    alias(libs.plugins.vanniktechPublish)
}

val publishMode = providers.gradleProperty("whisper.publishMode").getOrElse("kmp")

kotlin {
    applyDefaultHierarchyTemplate()
    
    androidLibrary {
        namespace = "com.whisper"
        compileSdk = 34
        minSdk = 29
    }
    
    val jvmTargetName = if (publishMode == "jvm") "jvm" else "desktop"
    jvm(jvmTargetName)

    if (publishMode == "kmp") {
        val xcf = XCFramework("Whisper")

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
                linkerOpts("-L${project(":whisper-dsp").projectDir}/prebuilt/ios-simulator/lib", "-lliquid")
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
                linkerOpts("-L${project(":whisper-dsp").projectDir}/prebuilt/ios-simulator/lib", "-lliquid")
            }
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
