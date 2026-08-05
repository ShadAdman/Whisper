plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    wasmJs {
        moduleName = "whisper-doc"
        browser {
            commonWebpackConfig {
                outputFileName = "whisper-doc.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
            }
        }
        val wasmJsMain by getting {
            dependencies {
                
            }
        }
    }
}

compose.experimental {
    web.application {}
}
