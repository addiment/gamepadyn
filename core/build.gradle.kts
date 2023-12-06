plugins {
    kotlin("jvm") version "1.9.0"
}

group = "computer.living"
version = "0.1.0"

kotlin {
    jvmToolchain(8)
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
//    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
    implementation("javax.validation:validation-api:2.0.1.Final")
}

tasks.test {
    useJUnitPlatform()
}