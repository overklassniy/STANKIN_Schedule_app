plugins {
    alias(libs.plugins.compose.compiler)
    id("com.android.library")
}

android {
    val appCompileSdkVersion = rootProject.extra["appCompileSdkVersion"] as Int
    val appMinSdkVersion = rootProject.extra["appMinSdkVersion"] as Int
    val appTargetSdkVersion = rootProject.extra["appTargetSdkVersion"] as Int

    compileSdk = appCompileSdkVersion
    namespace = "com.overklassniy.stankinschedule.core.ui"

    defaultConfig {
        minSdk = appMinSdkVersion


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

dependencies {
    implementation(project(":domain:core"))

    // Kotlin
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.paging)
    implementation(libs.compose.coil)
    implementation(libs.compose.activity)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.accompanist.permission)
    implementation(libs.ui.material)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.splashscreen)

    // Components
    implementation(libs.integration.browser)
}