import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")

    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}

android {

    val appCompileSdkVersion = rootProject.extra["appCompileSdkVersion"] as Int
    val appMinSdkVersion = rootProject.extra["appMinSdkVersion"] as Int
    val appTargetSdkVersion = rootProject.extra["appTargetSdkVersion"] as Int
    val appVersionCode = rootProject.extra["appVersionCode"] as Int
    val appVersionName = rootProject.extra["appVersionName"] as String
    val appBuildToolsVersion = rootProject.extra["appBuildToolsVersion"] as String

    compileSdk = appCompileSdkVersion
    buildToolsVersion = appBuildToolsVersion

    defaultConfig {

        applicationId = "com.overklassniy.stankinschedule"

        minSdk = appMinSdkVersion
        targetSdk = appTargetSdkVersion
        versionCode = appVersionCode
        versionName = appVersionName

        // setProperty("archivesBaseName", "stankin-schedule_v$versionName($versionCode)")

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Sentry DSN из local.properties
        val sentryDsn = rootProject.file("local.properties")
            .takeIf { it.exists() }
            ?.let { Properties().apply { it.inputStream().use { s -> load(s) } } }
            ?.getProperty("sentry.dsn", "")
            ?: ""
        buildConfigField("String", "SENTRY_DSN", "\"$sentryDsn\"")

        // Opt-in to annotation defaults for Kotlin 2.x
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
                arguments["room.expandProjection"] = "true"
            }
        }
    }

    signingConfigs {
        create("release") {
            val localProps = rootProject.file("local.properties")
                .takeIf { it.exists() }
                ?.let { Properties().apply { it.inputStream().use { s -> load(s) } } }
            storeFile = rootProject.file(localProps?.getProperty("RELEASE_STORE_FILE", "release.keystore") ?: "release.keystore")
            storePassword = localProps?.getProperty("RELEASE_STORE_PASSWORD", "") ?: ""
            keyAlias = localProps?.getProperty("RELEASE_KEY_ALIAS", "release") ?: "release"
            keyPassword = localProps?.getProperty("RELEASE_KEY_PASSWORD", "") ?: ""
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".debug"

            isDebuggable = true
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")

            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

            signingConfig = signingConfigs.getByName("debug")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "benchmark-rules.pro",
            )
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
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

    // kotlinOptions removed - handled by AGP 9 or tasks.withType<KotlinCompile> in root

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md",
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/groovy-release-info.properties",
            )
        )
        resources.merges.addAll(
            listOf(
                "META-INF/groovy/org.codehaus.groovy.runtime.ExtensionModule",
            )
        )
    }
    namespace = "com.overklassniy.stankinschedule"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

kotlin {
    compilerOptions {
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":domain:core"))
    implementation(project(":domain:news-core"))
    implementation(project(":domain:news-widget"))
    implementation(project(":domain:schedule-core"))
    implementation(project(":domain:schedule-settings"))

    implementation(project(":data:core"))
    implementation(project(":data:journal-core"))
    implementation(project(":data:schedule-core"))
    implementation(project(":data:schedule-ical"))
    implementation(project(":data:schedule-table"))
    implementation(project(":data:schedule-parser"))
    implementation(project(":data:schedule-repository"))
    implementation(project(":data:schedule-settings"))
    implementation(project(":data:schedule-viewer"))
    implementation(project(":data:schedule-widget"))
    implementation(project(":data:news-core"))
    implementation(project(":data:news-widget"))

    implementation(project(":ui:core"))
    implementation(project(":ui:home"))
    implementation(project(":ui:journal-login"))
    implementation(project(":ui:journal-predict"))
    implementation(project(":ui:journal-viewer"))
    implementation(project(":ui:schedule-creator"))
    implementation(project(":ui:schedule-editor"))
    implementation(project(":ui:schedule-table"))
    implementation(project(":ui:schedule-parser"))
    implementation(project(":ui:schedule-list"))
    implementation(project(":ui:schedule-repository"))
    implementation(project(":ui:schedule-viewer"))
    implementation(project(":ui:schedule-widget"))
    implementation(project(":ui:news-review"))
    implementation(project(":ui:news-widget"))
    implementation(project(":ui:settings"))


    // Core
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    implementation(libs.compose.coil)

    implementation(libs.compose.materialNavigation)

    // Components
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.ui.material)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Sentry
    implementation(libs.sentry.android)

    // Room DB
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Worker
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.work.hilt)
    ksp(libs.androidx.work.hiltCompiler)

    // DI
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.compiler)
}