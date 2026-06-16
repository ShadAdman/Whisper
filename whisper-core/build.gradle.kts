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
        
        // No jvmTarget here, it's handled differently or defaults
    }
    
    jvm("desktop")
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            // No dependencies
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
