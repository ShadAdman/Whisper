plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatform)
}

kotlin {
    applyDefaultHierarchyTemplate()
    
    androidLibrary {
        namespace = "com.whisper.audio"
        compileSdk = 34
        minSdk = 24
    }
    
    jvm("desktop")
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":whisper-core"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
