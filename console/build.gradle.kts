plugins {
    java
    id("org.springframework.boot") version "2.7.8"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.apache.logging.log4j:log4j-core:2.17.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.springframework.boot:spring-boot-starter:2.7.8")
    implementation("org.springframework.boot:spring-boot-starter-log4j2:2.7.8")
    implementation("com.datumbox:lpsolve:5.5.2.0")
    implementation(project(":shared"))
    implementation(project(":algorithm"))
}

configurations {
    all{
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

val bootRun by tasks.getting(JavaExec::class) {
    standardInput = System.`in`
}

tasks.getting(JavaCompile::class) {
    options.encoding = "UTF-8"
}
