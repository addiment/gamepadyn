plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
    id("com.android.library") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

val libVersion by extra { "0.3.0-BETA" }

allprojects {
    group = "computer.living.gamepadyn"
    version = libVersion

    repositories {
        mavenCentral()
        google()
    }

}

//android {
//    publishing {
//        singleVariant("release") {
//            withSourcesJar()
//        }
//    }
//}
