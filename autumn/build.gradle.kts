plugins {
    kotlin("jvm")
    id("maven-publish")
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.21-1.0.15")
}