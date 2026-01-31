plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

android {
    buildFeatures { buildConfig = true }
    val appCompileSdkVersion: Int by rootProject.extra
    val appMinSdkVersion: Int by rootProject.extra
    val appTargetSdkVersion: Int by rootProject.extra

    compileSdk = appCompileSdkVersion
    namespace = "com.overklassniy.stankinschedule.schedule.ical.data"

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
hilt { enableAggregatingTask = true }

dependencies {

    implementation(project(":domain:schedule-ical"))

    // Kotlin
    implementation(libs.androidx.core)

    implementation(libs.ical4j)

    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
    testImplementation(libs.junit)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}