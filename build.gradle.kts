import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidKotlinMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.vanniktechPublish) apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

subprojects {
    val publishMode = providers.gradleProperty("whisper.publishMode").getOrElse("kmp")

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    pluginManager.withPlugin("com.vanniktech.maven.publish") {
        val props = project.providers
        val artifactIdProp = props.gradleProperty("POM_ARTIFACT_ID").orNull
        val snapshotVersion = props.gradleProperty("PROJECT_VERSION_NAME").get()
        val computedVersion = System.getenv("GITHUB_REF")
            ?.takeIf { it.startsWith("refs/tags/") }
            ?.substringAfterLast("/")
            ?: snapshotVersion

        project.group = props.gradleProperty("PROJECT_GROUP").get()
        project.version = computedVersion

        extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {

            publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
            signAllPublications()

            artifactIdProp?.let{
                coordinates(project.group.toString(), it, project.version.toString())
            }

            pom {
                name.set(props.gradleProperty("POM_NAME"))
                description.set(props.gradleProperty("POM_DESCRIPTION"))
                url.set(props.gradleProperty("POM_URL"))
                licenses {
                    license {
                        name.set(props.gradleProperty("POM_LICENSE_NAME"))
                        url.set(props.gradleProperty("POM_LICENSE_URL"))
                    }
                }
                developers {
                    developer {
                        id.set(props.gradleProperty("POM_DEVELOPER_ID"))
                        name.set(props.gradleProperty("POM_DEVELOPER_NAME"))
                        email.set(props.gradleProperty("POM_DEVELOPER_EMAIL"))
                    }
                }
                scm {
                    connection.set(props.gradleProperty("SCM_CONNECTION"))
                    developerConnection.set(props.gradleProperty("SCM_DEVELOPER_CONNECTION"))
                    url.set(props.gradleProperty("POM_URL"))
                }
            }
        }

        extensions.configure<SigningExtension>("signing") {
            val keyId = System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyId")
            val key = System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey")
            val keyPassword = System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword")

            if (keyId != null && key != null && keyPassword != null) {
                useInMemoryPgpKeys(key, keyPassword)
            }
        }
    }
}
