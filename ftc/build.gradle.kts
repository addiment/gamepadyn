////apply(plugin("org.jetbrains.kotlin.android"))
//
//
//plugins {
////    kotlin("jvm") version "1.8.20"
////    kotlin("jvm") version "1.9.0"
////    id("java")
//
//    //    id("com.android.application") version "8.2.0" apply false
////    id("com.android.library") version "8.2.0" apply false
////    id("org.jetbrains.kotlin.android") version "1.5.31" apply false
//
//    id("com.android.library")
//    id("org.jetbrains.kotlin.android")
//}
//
//android {
//    namespace = "computer.living.gamepadyn.ftc"
//    compileSdk = 33
//
//    defaultConfig {
//        minSdk 24
//
//        // "Google Play requires that apps target API level 31 or higher."
//        // ok and???
//        //noinspection ExpiredTargetSdkVersion
//        targetSdk 28
//
//        consumerProguardFiles "consumer-rules.pro"
//    }
//
//    buildTypes {
//        release {
//            minifyEnabled false
//            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
//        }
//    }
//    compileOptions {
//        sourceCompatibility JavaVersion.VERSION_1_8
//                targetCompatibility JavaVersion.VERSION_1_8
//    }
//    kotlinOptions {
//        jvmTarget = '1.8'
//    }
//}
//
//group = "computer.living"
//version = "0.1.0"
//
//repositories {
//    mavenCentral()
//    google()
//}
//
//kotlin {
//    jvmToolchain(8)
//}
//
//dependencies {
//    implementation(project(":core"))
////    // https://mvnrepository.com/artifact/org.firstinspires.ftc/FtcCommon
////    runtimeOnly("org.firstinspires.ftc:FtcCommon:9.0.1")
//////    implementation("org.firstinspires.ftc:FtcCommon:9.0.1")
////    // https://mvnrepository.com/artifact/org.firstinspires.ftc/RobotCore
////    runtimeOnly("org.firstinspires.ftc:RobotCore:9.0.1")
//////    implementation("org.firstinspires.ftc:RobotCore:9.0.1")
//    implementation("org.firstinspires.ftc:RobotCore:aar:9.0.1")
//    implementation("org.firstinspires.ftc:RobotServer:9.0.1")
//    implementation("org.firstinspires.ftc:OnBotJava:9.0.1")
//    implementation("org.firstinspires.ftc:Hardware:9.0.1")
//    implementation("org.firstinspires.ftc:FtcCommon:9.0.1")
//    implementation("org.firstinspires.ftc:Vision:9.0.1")
//    implementation("org.firstinspires.ftc:gameAssets-CenterStage:1.0.0")
//    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.3")
//    runtimeOnly("org.tensorflow:tensorflow-lite:2.14.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//}
