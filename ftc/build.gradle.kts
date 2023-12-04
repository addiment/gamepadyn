plugins {
    kotlin("jvm") version "1.8.20"
//    id("com.android.application") version "8.2.0" apply false
//    id("com.android.library") version "8.2.0" apply false
//    id("org.jetbrains.kotlin.android") version "1.5.31" apply false
}

group = "computer.living"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("org.firstinspires.ftc:FtcCommon:9.0.1")
    implementation("org.firstinspires.ftc:RobotCore:9.0.1")
}

kotlin {
    jvmToolchain(8)
}