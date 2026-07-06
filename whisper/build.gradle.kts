plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatform)
}

kotlin {
    jvmToolchain(17)
    applyDefaultHierarchyTemplate()
    
    androidLibrary {
        namespace = "com.whisper"
        compileSdk = 34
        minSdk = 29
    }
    
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
            api(project(":whisper-core"))
            implementation(project(":whisper-dsp"))
            implementation(project(":whisper-audio"))
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
