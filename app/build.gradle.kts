plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.financetracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.financetracker"
        minSdk = 26 // Change this to 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true;
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro" // Specify your ProGuard rules file
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Core Android Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.storage)
    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Firebase Libraries (use BOM for version management)
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-auth") // Use BOM for version management
    implementation("com.google.firebase:firebase-storage") // Use BOM for version management
    implementation("com.google.firebase:firebase-database") // Use BOM for version management
    implementation("com.google.firebase:firebase-firestore") // Use BOM for version management
    implementation("com.google.firebase:firebase-dynamic-links") // Use BOM for version management
    implementation("com.google.firebase:firebase-analytics") // Use BOM for version management

    // Google Play Services for Auth
    implementation("com.google.android.gms:play-services-auth") // Use BOM for version management

}