// Top-level build file where you can add configuration options common to all subprojects/modules.

ext {
    extra["appCompileSdkVersion"] = 35
    extra["appMinSdkVersion"] = 26
    extra["appTargetSdkVersion"] = 35
    extra["appVersionCode"] = 125
    extra["appVersionName"] = "2.3.0"
    extra["appBuildToolsVersion"] = "36.0.0"
}

buildscript {

    repositories {
        mavenCentral()
        mavenLocal()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.androidGradlePlugin)
        classpath(libs.kotlinPlugin)
        classpath(libs.googleServicesPlugin)
        classpath(libs.firebase.plugin)
        classpath(libs.hilt.plugin)
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
}

allprojects {

    repositories {
        mavenCentral()
        google()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://jitpack.io")
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper> {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension> {
            jvmToolchain(21)
        }
    }

    tasks.withType<JavaCompile> {
        val javaToolchains = project.extensions.getByType<JavaToolchainService>()
        javaCompiler.set(javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        })
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
