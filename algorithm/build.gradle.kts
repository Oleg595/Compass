/*
 * This file was generated by the Gradle "init" task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("java-library")
    id("org.springframework.boot") version("2.7.8") apply false
    id("io.spring.dependency-management") version("1.1.2")
    id("jacoco")
}

description = "algorithm"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    implementation("org.springframework.boot:spring-boot:2.7.8")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.logging.log4j:log4j-api:2.21.1")
    implementation("org.apache.logging.log4j:log4j-core:2.21.1")
    implementation("org.ejml:ejml-all:0.43.1")
    implementation("com.datumbox:lpsolve:5.5.2.0")
    runtimeOnly("org.springframework.boot:spring-boot-devtools:2.7.8")

    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.8")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.mockito:mockito-core:5.3.1")
    implementation(project(":shared"))
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.jacocoTestReport {
    reports {
//        xml.isEnabled = false
//        csv.isEnabled = false
//        html.isEnabled = true
//        html.destination = file("$buildDir/reports/coverage")
    }
}

val testCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage."

    dependsOn(":test", ":jacocoTestReport", ":jacocoTestCoverageVerification")
    val jacocoTestReport = tasks.findByName("jacocoTestReport")
    jacocoTestReport?.mustRunAfter(tasks.findByName("test"))
    tasks.findByName("jacocoTestCoverageVerification")?.mustRunAfter(jacocoTestReport)
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11
