import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "com.lam.pedro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lam.pedro"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Set value part
        // Set value part
        val properties = Properties().apply {
            load(project.rootProject.file("local.properties").inputStream())
        }

        buildConfigField("String", "SUPABASE_KEY", "\"${properties.getProperty("SUPABASE_KEY")}\"")
        buildConfigField("String", "SUPABASE_URL", "\"${properties.getProperty("SUPABASE_URL")}\"")
        buildConfigField(
            "String",
            "SUPABASE_SERVICE_ROLE",
            "\"${properties.getProperty("SUPABASE_SERVICE_ROLE")}\""
        )
        buildConfigField("String", "MAPLIBRE_ACCESS_TOKEN", "\"${properties.getProperty("MAPLIBRE_ACCESS_TOKEN")}\"")

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    // Kotlin
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.benchmark.common)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.play.services.location)
    implementation(libs.core)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.storage)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)


    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.accompanist.permissions)


    // Supabase
    //noinspection UseTomlInstead
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.1"))
    implementation(libs.supabase.storage)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.coil.compose)


    //noinspection UseTomlInstead
    implementation("androidx.health.connect:connect-client:1.1.0-alpha10")
//    implementation(libs.androidx.compose.animation)

//    implementation("androidx.compose.ui:ui:1.7.0-alpha07")
//    implementation("androidx.compose.animation:animation:1.7.0-alpha07")

//    implementation("com.airbnb.android:lottie-compose:4.2.0")

    implementation(libs.mapLibre)
    // AirBnB Lottie
    implementation(libs.lottie.compose)

    // Accompanist
    implementation(libs.accompanist.placeholder.material)

    // charts
    implementation(libs.compose.charts)
}



