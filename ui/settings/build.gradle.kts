plugins {
    alias(libs.plugins.compose.compiler)
    id("com.android.library")
    // id("org.jetbrains.kotlin.android")

    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

android {
    val appVersionName = rootProject.extra["appVersionName"] as String
    val appCompileSdkVersion = rootProject.extra["appCompileSdkVersion"] as Int
    val appMinSdkVersion = rootProject.extra["appMinSdkVersion"] as Int
    val appTargetSdkVersion = rootProject.extra["appTargetSdkVersion"] as Int

    compileSdk = appCompileSdkVersion
    namespace = "com.overklassniy.stankinschedule.settings.ui"

    defaultConfig {
        minSdk = appMinSdkVersion


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        debug {
            buildConfigField("String", "APP_VERSION", "\"$appVersionName\"")
            buildConfigField("boolean", "DEBUG", "true")
        }
        release {
            buildConfigField("String", "APP_VERSION", "\"$appVersionName\"")
            buildConfigField("boolean", "DEBUG", "false")

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
        compose = true
        buildConfig = true
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":ui:core"))
    implementation(project(":domain:core"))

    implementation(project(":ui:schedule-core"))
    implementation(project(":ui:schedule-widget"))
    implementation(project(":domain:schedule-settings"))

    // Kotlin
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    implementation(libs.compose.navigation)

    // Components
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.ui.material)

    implementation(libs.compose.color.picker.android)

    // Network
    implementation(libs.okhttp)
    implementation(libs.androidx.webkit)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}