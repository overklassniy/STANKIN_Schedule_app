plugins {
    id("com.android.library")
    // id("org.jetbrains.kotlin.android")

    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

android {
    buildFeatures { buildConfig = true }
    val appCompileSdkVersion: Int by rootProject.extra
    val appMinSdkVersion: Int by rootProject.extra
    val appTargetSdkVersion: Int by rootProject.extra

    compileSdk = appCompileSdkVersion
    namespace = "com.overklassniy.stankinschedule.schedule.repository.data"

    defaultConfig {
        minSdk = appMinSdkVersion


        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }

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
    implementation(project(":data:core"))
    implementation(project(":data:schedule-core"))
    implementation(project(":domain:core"))
    implementation(project(":domain:schedule-core"))
    implementation(project(":domain:schedule-repository"))

    // Kotlin
    implementation(libs.androidx.core)

    // Network
    implementation(libs.bundles.network)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage)

    // Room DB
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Worker
    implementation(libs.work.runtime)
    implementation(libs.work.hilt)
    ksp(libs.work.hiltCompiler)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}