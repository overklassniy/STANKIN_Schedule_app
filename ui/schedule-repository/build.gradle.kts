plugins {
    alias(libs.plugins.compose.compiler)
    id("com.android.library")
    // id("org.jetbrains.kotlin.android")

    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

android {
    val appCompileSdkVersion: Int by rootProject.extra
    val appMinSdkVersion: Int by rootProject.extra
    val appTargetSdkVersion: Int by rootProject.extra

    compileSdk = appCompileSdkVersion
    namespace = "com.overklassniy.stankinschedule.schedule.repository.ui"

    defaultConfig {
        minSdk = appMinSdkVersion


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

// Allow references to generated code

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":ui:core"))
    implementation(project(":ui:schedule-parser"))
    implementation(project(":data:core"))
    implementation(project(":domain:core"))
    implementation(project(":domain:schedule-core"))
    implementation(project(":domain:schedule-repository"))

    // Kotlin
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    implementation(libs.compose.runtime.livedata)

    // Components
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.ui.material)

    // Network
    implementation(libs.bundles.network)

    // Room DB
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Worker
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.work.hilt)
    ksp(libs.androidx.work.hiltCompiler)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}