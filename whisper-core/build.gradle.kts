plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatform)
}

kotlin {
    applyDefaultHierarchyTemplate()
    
    androidLibrary {
        namespace = "com.whisper.core"
        compileSdk = 34
        minSdk = 24
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
            // No dependencies
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
