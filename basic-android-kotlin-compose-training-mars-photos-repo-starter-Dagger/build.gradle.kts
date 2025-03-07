// build.gradle (Nivel de proyecto)

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Plugin de Dagger Hilt
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
    }
}

plugins {
    id("com.android.application") version "8.1.3" apply false
    id("com.android.library") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}
