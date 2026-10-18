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
}
