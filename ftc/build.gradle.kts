buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.0")
    }
}

pluginManagement {
    reposityories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.android.library") version "8.2.0" apply false
    id("com.android.application") version "8.2.0" apply false
    kotlin("jvm") version "1.9.0"
    id("java-library")
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

kotlin {
    jvmToolchain(8)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(project(":core"))
//    // https://mvnrepository.com/artifact/org.firstinspires.ftc/FtcCommon
//    runtimeOnly("org.firstinspires.ftc:FtcCommon:9.0.1")
////    implementation("org.firstinspires.ftc:FtcCommon:9.0.1")
//    // https://mvnrepository.com/artifact/org.firstinspires.ftc/RobotCore
//    runtimeOnly("org.firstinspires.ftc:RobotCore:9.0.1")
////    implementation("org.firstinspires.ftc:RobotCore:9.0.1")
    implementation("org.firstinspires.ftc:RobotCore:9.0.1:aar")
    implementation("org.firstinspires.ftc:Hardware:9.0.1")
    implementation("org.firstinspires.ftc:FtcCommon:9.0.1")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.3")
    runtimeOnly("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
}
