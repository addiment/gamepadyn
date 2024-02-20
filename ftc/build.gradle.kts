import org.gradle.kotlin.dsl.extra

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

android {
    namespace = "computer.living.gamepadyn.ftc"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        aarMetadata {
            minCompileSdk = 24
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

//    productFlavors {
//        register("lemonade") {
//            aarMetadata {
//                minCompileSdk = 24
//            }
//        }
//    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "computer.living.gamepadyn"
            artifactId = "ftc"
            version = version

            afterEvaluate {
                from(components["release"])
            }

//            pom {
//                description = "An input handling library."
//                scm {
//                    connection = "scm:git:git://github.com/addiment/gamepadyn.git"
//                    developerConnection = "scm:git:ssh://github.com/addiment/gamepadyn.git"
//                    url = "http://github.com/addiment/gamepadyn"
//                }
//            }
        }
    }

    repositories {
        maven {
            name = "mavenLocalRepo"
            url = uri("${rootDir}/repo")
        }
    }
}

dependencies {
//    implementation("androidx.core:core-ktx:1.9.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("org.firstinspires.ftc:RobotCore:9.0.1")
    implementation("org.firstinspires.ftc:Hardware:9.0.1")
    implementation("org.firstinspires.ftc:FtcCommon:9.0.1")
    implementation(project(":core"))
    testImplementation(kotlin("test"))
}