plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.alethra.browser"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.alethra.browser"
        minSdk = 24
        targetSdk = 34
        val run = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1
        versionCode = run
        versionName = "1.$run"
        // sherpa-onnx dan ML Kit membawa library native; batasi ke ABI HP modern supaya APK tidak membengkak
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a") }
    }
    packaging {
        jniLibs { pickFirsts += listOf("**/libc++_shared.so") }
    }
    signingConfigs {
        create("alethra") {
            storeFile = file("alethra.jks")
            storePassword = "alethra123"
            keyAlias = "alethra"
            keyPassword = "alethra123"
        }
    }
    buildTypes {
        getByName("debug") { signingConfig = signingConfigs.getByName("alethra") }
        getByName("release") { signingConfig = signingConfigs.getByName("alethra") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation("androidx.webkit:webkit:1.11.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // Suara ke teks offline: sherpa-onnx (AAR diunduh oleh workflow ke app/libs, lihat .github/workflows/build.yml)
    implementation(files("libs/sherpa-onnx.aar"))
    // Terjemahan offline (model per bahasa diunduh dari dalam aplikasi) dan deteksi bahasa
    implementation("com.google.mlkit:translate:17.0.3")
    implementation("com.google.mlkit:language-id:17.0.6")
}
