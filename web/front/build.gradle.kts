plugins {
    id("java")
    kotlin("multiplatform") version "1.8.22"
    kotlin("plugin.serialization") version "1.8.22"
}

description = "front"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                devServer?.port = 3000
            }
        }
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.430"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-tanstack-react-table")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
                implementation(npm("react-graph-vis", "0.1.0"))
                implementation(npm("react-modal", "3.14.4"))

                // Зависимости от других модулей
                implementation(project(":shared"))
            }
        }
    }
}
