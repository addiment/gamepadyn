import org.gradle.kotlin.dsl.extra

plugins {
    kotlin("jvm")
    `maven-publish`
    `java-library`
}

dependencies {
    testImplementation(kotlin("test"))
//    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

//test {
//    useJUnitPlatform()
//}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "computer.living.gamepadyn"
            artifactId = "core"
            version = version

            from(components["java"])

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
}