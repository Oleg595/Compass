val serialization_version = "0.14.0"

plugins {
    kotlin("multiplatform") version "1.8.22"
    kotlin("plugin.serialization") version "1.8.22"
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js(IR) {
        nodejs()
    }

    /* Targets configuration omitted.
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0")
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0")
            }
        }

        js().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-js:1.5.0")
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")
            }
        }
    }
}
