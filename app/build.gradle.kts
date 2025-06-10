plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bugjournal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.bugjournal"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation ("com.google.android.gms:play-services-auth:20.6.0")
    implementation ("com.google.firebase:firebase-auth-ktx:22.1.1")
    implementation(libs.androidx.core.ktx)
    implementation("androidx.compose.material3:material3:1.4.0-alpha15")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("androidx.compose.ui:ui:1.6.0")
    implementation ("androidx.compose.material3:material3:1.1.2")
    implementation ("androidx.activity:activity-compose:1.8.2")
    implementation ("androidx.navigation:navigation-compose:2.7.1")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("androidx.compose.material:material:1.6.1")
    implementation ("androidx.compose.material:material-icons-extended:1.6.1")
    implementation ("androidx.compose.material3:material3:1.2.1")
    implementation ("com.airbnb.android:lottie-compose:6.1.0")






}