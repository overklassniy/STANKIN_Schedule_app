plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}

android {

    val appCompileSdkVersion: Int by rootProject.extra
    val appMinSdkVersion: Int by rootProject.extra
    val appTargetSdkVersion: Int by rootProject.extra
    val appVersionCode: Int by rootProject.extra
    val appVersionName: String by rootProject.extra
    val appBuildToolsVersion: String by rootProject.extra

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
        
        // Opt-in to annotation defaults for Kotlin 2.x
        javaCompileOptions {
             annotationProcessorOptions {
                 arguments["room.schemaLocation"] = "$projectDir/schemas"
                 arguments["room.incremental"] = "true"
                 arguments["room.expandProjection"] = "true"
             }
        }
        
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
        }
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.add("-Xannotation-default-target=param-property")
        }
    }


    buildTypes {
        debug {
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".debug"

            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
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

    // kotlinOptions removed - handled by AGP 9 or tasks.withType<KotlinCompile> in root

    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md",
            )
        )
    }
    namespace = "com.overklassniy.stankinschedule"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":domain:core"))
    implementation(project(":domain:news-core"))
    implementation(project(":domain:schedule-core"))
    implementation(project(":domain:schedule-settings"))
    implementation(project(":domain:settings"))

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
    implementation(project(":data:settings"))

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
    implementation(project(":ui:news-viewer"))
    implementation(project(":ui:settings"))


    // Core
    implementation(libs.androidx.core)

    // Jetpack Compose & Material 3
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3)
    implementation(libs.compose.activity)
    implementation(libs.compose.coil)

    implementation("androidx.compose.material:material-navigation:1.7.6")

    // Components
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.java8)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.splashscreen)
    implementation(libs.ui.material)

    implementation(libs.integration.googleServices)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
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
    implementation(libs.hilt.navigation)
    ksp(libs.hilt.compiler)
}