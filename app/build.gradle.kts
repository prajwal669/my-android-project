plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}
android {
    namespace = "com.example.yakshgana2"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.yakshgana2"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    dependencies {

        // Basic Android
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1")

        // Jetpack Compose
        implementation("androidx.activity:activity-compose:1.8.2")
        implementation("androidx.compose.ui:ui:1.6.0")
        implementation("androidx.compose.material3:material3:1.2.0")

        // Firebase (we use later)
        implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")

        // Google Maps (for tracker screen)
        implementation("com.google.android.gms:play-services-maps:18.2.0")

        // Audio player
        implementation("com.google.android.exoplayer:exoplayer:2.19.1")

        // Internet API (Gemini)
        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        implementation("androidx.compose.material:material-icons-extended")
        implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation("androidx.media3:media3-exoplayer:1.3.1")
        implementation("androidx.media3:media3-ui:1.3.1")
        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        implementation("com.google.firebase:firebase-auth-ktx")

    }
}