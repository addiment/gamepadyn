plugins {
    kotlin("jvm") version "1.9.21"
    id("com.android.library") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

allprojects {
    group = "computer.living"
    version = "0.1.0-BETA"

    repositories {
        mavenCentral()
        google()
    }
}