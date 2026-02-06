plugins {
    id("com.android.library")
    // id("org.jetbrains.kotlin.android")

    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
}

// Загрузка RSS URL из stankin.secret (вне android {}, чтобы не путать receiver в defaultConfig)
val rssSecrets: Map<String, String> = run {
    val secretFile = rootProject.file("stankin.secret")
    if (!secretFile.exists()) return@run emptyMap<String, String>()
    secretFile.readLines()
        .mapNotNull { line ->
            val parts = line.split("=", limit = 2)
            if (parts.size == 2) parts[0].trim() to parts[1].trim() else null
        }
        .toMap()
}

android {
    buildFeatures { buildConfig = true }
    val appCompileSdkVersion: Int by rootProject.extra
    val appMinSdkVersion: Int by rootProject.extra
    val appTargetSdkVersion: Int by rootProject.extra

    compileSdk = appCompileSdkVersion
    namespace = "com.overklassniy.stankinschedule.news.core.data"

    defaultConfig {
        minSdk = appMinSdkVersion

        if (rssSecrets.isNotEmpty()) {
            buildConfigField("String", "NEWS_RSS_URL", "\"${rssSecrets["NEWS_RSS_URL"] ?: ""}&LIMIT=100\"")
            buildConfigField("String", "ADS_RSS_URL", "\"${rssSecrets["ADS_RSS_URL"] ?: ""}&LIMIT=12\"")
            buildConfigField("String", "EXCHANGE_RSS_URL", "\"${rssSecrets["EXCHANGE_RSS_URL"] ?: ""}&LIMIT=12\"")
        } else {
            buildConfigField("String", "NEWS_RSS_URL", "\"\"")
            buildConfigField("String", "ADS_RSS_URL", "\"\"")
            buildConfigField("String", "EXCHANGE_RSS_URL", "\"\"")
        }

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
    implementation(project(":domain:core"))

    implementation(project(":domain:news-core"))

    // Kotlin
    implementation(libs.androidx.core)

    // Paging
    implementation(libs.androidx.paging)

    // Network
    implementation(libs.bundles.network)

    // Room DB
    implementation(libs.bundles.room)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}