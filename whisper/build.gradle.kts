plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatform)
    alias(libs.plugins.vanniktechPublish)
}

kotlin {
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
            api(project(":whisper-audio"))
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
