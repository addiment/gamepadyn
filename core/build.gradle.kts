plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation(kotlin("test"))
//    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    // Use the native JUnit support of Gradle.
    "test"(Test::class) {
        useJUnit()
        useJUnitPlatform()
    }
}

kotlin {
    jvmToolchain(8)
}

//test {
//    useJUnitPlatform()
//}