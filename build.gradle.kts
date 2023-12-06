allprojects {
    repositories {
        mavenCentral()
        google()

        flatDir {
            dirs("libs")
        }
    }
}