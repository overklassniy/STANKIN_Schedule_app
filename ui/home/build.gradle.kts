plugins {
    alias(libs.plugins.compose.compiler)
    id("com.android.library")

    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

android {
    val appCompileSdkVersion = rootProject.extra["appCompileSdkVersion"] as Int
    val appMinSdkVersion = rootProject.extra["appMinSdkVersion"] as Int
    val appTargetSdkVersion = rootProject.extra["appTargetSdkVersion"] as Int
    val appVersionName = rootProject.extra["appVersionName"] as String

    compileSdk = appCompileSdkVersion
    namespace = "com.overklassniy.stankinschedule.home.ui"

    defaultConfig {
        minSdk = appMinSdkVersion

        buildConfigField("String", "APP_VERSION", "\"$appVersionName\"")

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

hilt { enableAggregatingTask = true }

dependencies {
    implementation(project(":ui:core"))
    implementation(project(":domain:core"))

    implementation(project(":ui:news-review"))
    implementation(project(":domain:news-core"))

    implementation(project(":ui:schedule-core"))
    implementation(project(":domain:schedule-viewer"))
    implementation(project(":domain:schedule-settings"))
    implementation(project(":domain:schedule-core"))


    // Kotlin
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    implementation(libs.compose.coil)

    // Components
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.ui.material)

    implementation(libs.integration.inAppUpdate)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}