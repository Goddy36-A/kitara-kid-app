buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24")
    }
}

// Project-level dependency repositories are declared once, centrally, in
// settings.gradle.kts (dependencyResolutionManagement). Declaring them again
// here via `allprojects { repositories { ... } }` conflicts with that
// FAIL_ON_PROJECT_REPOS setting and breaks the build.
