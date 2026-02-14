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

val BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

/**
 * Обфусцирует строку через XOR + Base64, чтобы URL не были видны в открытом виде
 * при декомпиляции APK. Ключ XOR задаётся параметром [key].
 */
fun obfuscate(input: String, key: Int = 0x5A): String {
    val xored = input.toByteArray(Charsets.UTF_8).map { (it.toInt() xor key).toByte() }.toByteArray()
    return base64Encode(xored)
}

fun base64Encode(input: ByteArray): String {
    val sb = StringBuilder()
    var i = 0
    while (i < input.size) {
        val b1 = input[i].toInt() and 0xFF
        val b2 = if (i + 1 < input.size) input[i + 1].toInt() and 0xFF else 0
        val b3 = if (i + 2 < input.size) input[i + 2].toInt() and 0xFF else 0
        sb.append(BASE64_CHARS[b1 shr 2])
        sb.append(BASE64_CHARS[((b1 and 3) shl 4) or (b2 shr 4)])
        sb.append(if (i + 1 < input.size) BASE64_CHARS[((b2 and 15) shl 2) or (b3 shr 6)] else '=')
        sb.append(if (i + 2 < input.size) BASE64_CHARS[b3 and 63] else '=')
        i += 3
    }
    return sb.toString()
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
            val newsUrl = obfuscate("${rssSecrets["NEWS_RSS_URL"] ?: ""}&LIMIT=100")
            val adsUrl = obfuscate("${rssSecrets["ADS_RSS_URL"] ?: ""}&LIMIT=12")
            val exchangeUrl = obfuscate("${rssSecrets["EXCHANGE_RSS_URL"] ?: ""}&LIMIT=12")
            buildConfigField("String", "NEWS_RSS_URL", "\"$newsUrl\"")
            buildConfigField("String", "ADS_RSS_URL", "\"$adsUrl\"")
            buildConfigField("String", "EXCHANGE_RSS_URL", "\"$exchangeUrl\"")
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