plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlinx.kover") version "0.9.0"
}

android {
    namespace = "com.koupa.barberbooking"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.koupa.barberbooking"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        // Build config - loaded from local.properties or environment
        val supabaseUrl = project.findProperty("SUPABASE_URL") as? String 
            ?: System.getenv("SUPABASE_URL") 
            ?: ""
        val supabaseAnonKey = project.findProperty("SUPABASE_ANON_KEY") as? String 
            ?: System.getenv("SUPABASE_ANON_KEY") 
            ?: ""
        
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
    }

    signingConfigs {
        create("release") {
            // Signing config loaded from local.properties or environment
            storeFile = file(project.findProperty("KEYSTORE_FILE") as? String ?: "koupa-release.jks")
            storePassword = project.findProperty("KEYSTORE_PASSWORD") as? String 
                ?: System.getenv("KEYSTORE_PASSWORD") 
                ?: ""
            keyAlias = project.findProperty("KEY_ALIAS") as? String ?: "koupa"
            keyPassword = project.findProperty("KEY_PASSWORD") as? String 
                ?: System.getenv("KEY_PASSWORD") 
                ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Supabase Kotlin SDK
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.2.2")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.2.2")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.2.2")
    implementation("io.github.jan-tennert.supabase:functions-kt:2.2.2")
    implementation("io.ktor:ktor-client-android:2.3.8")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    // Google Location Services (for GPS tracking)
    implementation("com.google.android.gms:play-services-location:21.1.0")
    // OSMDroid — free OpenStreetMap SDK (replaces Google Maps)
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    // Accompanist Permissions (location)
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    // Coil image loading (shop profile photos on map markers)
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Kotlinx Serialization (required by Supabase SDK)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // DataStore for preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Room Database for offline-first
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.9.3")
    testImplementation("io.kotest:kotest-assertions:5.9.3")
    testImplementation("io.kotest:kotest-property:5.9.3")

    // MockK
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("io.mockk:mockk-agent-jvm:1.13.9")

    // Coroutine testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Kover coverage
    testImplementation("org.jetbrains.kotlinx:kover:0.9.7")
}
