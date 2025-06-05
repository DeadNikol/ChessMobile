// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "2.1.21-2.0.1" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.0" apply false
    id ("com.android.library") version "7.2.2" apply false
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.0")
        classpath("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    }
}
