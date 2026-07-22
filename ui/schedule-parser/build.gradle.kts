plugins {
    alias(libs.plugins.compose.compiler)
    id("com.android.library")
    // id("org.jetbrains.kotlin.android")

    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

android {
    val appCompileSdkVersion = rootProject.extra["appCompileSdkVersion"] as Int
    val appMinSdkVersion = rootProject.extra["appMinSdkVersion"] as Int
    val appTargetSdkVersion = rootProject.extra["appTargetSdkVersion"] as Int

    compileSdk = appCompileSdkVersion
    namespace = "com.overklassniy.stankinschedule.schedule.parser.ui"

    defaultConfig {
        minSdk = appMinSdkVersion


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":ui:core"))
    implementation(project(":ui:schedule-core"))
    implementation(project(":ui:schedule-table"))
    implementation(project(":domain:core"))
    implementation(project(":domain:schedule-core"))
    implementation(project(":domain:schedule-parser"))
    implementation(project(":domain:schedule-settings"))
    implementation(project(":domain:schedule-table"))

    // Kotlin
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)

    implementation(libs.accompanist.permission)

    // Components
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.ui.material)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}