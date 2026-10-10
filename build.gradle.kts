// Top-level build file where you can add configuration options common to all subprojects/modules.

ext {
    extra["appCompileSdkVersion"] = 37
    extra["appMinSdkVersion"] = 26
    extra["appTargetSdkVersion"] = 36
    extra["appVersionCode"] = 340
    extra["appVersionName"] = "3.4.0"
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
        classpath(libs.hilt.plugin)
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.detekt) apply false
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
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        }
    }

    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper> {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension> {
            jvmToolchain(25)
        }
    }

    tasks.withType<JavaCompile> {
        val javaToolchains = project.extensions.getByType<JavaToolchainService>()
        javaCompiler.set(javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(25))
        })
    }

    apply(plugin = "dev.detekt")
    extensions.configure<dev.detekt.gradle.extensions.DetektExtension> {
        buildUponDefaultConfig.set(true)
        parallel.set(true)
        ignoreFailures.set(true)
        config.setFrom(rootProject.layout.projectDirectory.file("detekt.yml"))
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
