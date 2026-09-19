import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {

    alias(libs.plugins.kotlinMultiplatform)

    alias(libs.plugins.androidApplication)

    alias(libs.plugins.composeMultiplatform)

    alias(libs.plugins.composeCompiler)

    kotlin("plugin.serialization")

    alias(libs.plugins.googleServices)

    alias(libs.plugins.firebaseCrashlytics)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation("androidx.fragment:fragment-ktx:1.8.5")
            implementation(libs.media3.exoplayer)
            implementation("androidx.media3:media3-session:1.4.1")
            implementation("androidx.media3:media3-ui:1.4.1")
            implementation("androidx.work:work-runtime-ktx:2.9.0")
            implementation("org.slf4j:slf4j-simple:2.0.13")
            implementation("androidx.glance:glance-appwidget:1.1.1")
            implementation("androidx.glance:glance-material3:1.1.1")
            implementation("androidx.datastore:datastore-preferences:1.1.1")

            // Firebase Android SDKs via BOM
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.firestore)
            implementation("com.google.firebase:firebase-auth-ktx")
            implementation("com.google.firebase:firebase-appcheck-playintegrity")
            implementation("com.google.firebase:firebase-appcheck-debug")
            implementation("com.google.android.play:integrity:1.4.0")
            implementation("com.google.android.gms:play-services-auth:21.2.0")
        }
        commonMain.dependencies {
            implementation(projects.shared.models)
            implementation(projects.shared.domain)
            implementation(projects.shared.data)
            implementation(projects.shared.audioSync)
            implementation(projects.shared.streak)
            implementation(projects.shared.reminders)
            implementation(projects.shared.analytics)
            implementation(projects.shared.gamification)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}

android {
    namespace = "com.creativeblisslabs.nitnemguktasteek"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    val keystorePropertiesFile = rootProject.file("local.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
    }

    signingConfigs {
        create("release") {
            val keyPath = keystoreProperties.getProperty("RELEASE_KEYSTORE_PATH") ?: ""
            if (keyPath.isNotEmpty()) {
                val resolvedKeyFile = rootProject.file(keyPath).let { if (it.exists()) it else file(keyPath) }
                storeFile = resolvedKeyFile
                storePassword = keystoreProperties.getProperty("RELEASE_KEYSTORE_PASSWORD") 
                    ?: keystoreProperties.getProperty("RELEASE_STORE_PASSWORD") ?: ""
                keyAlias = keystoreProperties.getProperty("RELEASE_KEY_ALIAS") ?: ""
                keyPassword = keystoreProperties.getProperty("RELEASE_KEY_PASSWORD") ?: ""
            }
        }
    }

    defaultConfig {
        applicationId = "com.creativeblisslabs.nitnemguktasteek"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            val keyPath = keystoreProperties.getProperty("RELEASE_KEYSTORE_PATH") ?: ""
            if (keyPath.isNotEmpty()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.creativeblisslabs.nitnemguktasteek.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "Nitnem Gutka Steek"
            packageVersion = "1.0.0"
            description = "A peaceful Nitnem companion for daily reading and listening."
            vendor = "Audiocular"
        }
    }
}
