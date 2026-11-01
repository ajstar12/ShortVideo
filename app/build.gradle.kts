plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt") // Apply the Kapt plugin
}

android {
    namespace = "com.arjunsinh.shortvideo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.arjunsinh.shortvideo"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.02"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
//    implementation("androidx.activity:activity:1.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Library for json data convert
    implementation("com.google.code.gson:gson:2.11.0")

    //Library for play video
//    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation ("androidx.media3:media3-exoplayer:1.3.1")
    implementation ("androidx.media3:media3-ui:1.3.1")


    //Library for multi display support // for Width and Height
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    //Library for multi display support // for Text Size
    implementation("com.intuit.ssp:ssp-android:1.1.0")

    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1") // For Kotlin Extensions and Coroutines support

    // Kotlin Coroutine
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1") // Check for the latest version
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1") // Check for the latest version

    implementation ("com.google.android.play:app-update:2.1.0")

}