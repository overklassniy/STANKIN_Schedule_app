plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

android {
    buildFeatures { buildConfig = true }
    val appCompileSdkVersion = rootProject.extra["appCompileSdkVersion"] as Int
    val appMinSdkVersion = rootProject.extra["appMinSdkVersion"] as Int
    val appTargetSdkVersion = rootProject.extra["appTargetSdkVersion"] as Int

    compileSdk = appCompileSdkVersion
    namespace = "com.overklassniy.stankinschedule.schedule.parser.data"

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
}
hilt { enableAggregatingTask = true }

dependencies {
    implementation(project(":domain:schedule-parser"))

    // Kotlin
    implementation(libs.androidx.core)
    implementation(libs.androidx.documentfile)

    implementation(libs.pdfbox.android)

    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
    testImplementation(libs.junit)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}