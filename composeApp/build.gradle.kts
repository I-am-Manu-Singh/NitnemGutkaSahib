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
            freeCompilerArgs.addAll("-Xskip-metadata-version-check", "-Xskip-prerelease-check")
        }
    }

    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")

    // Configure a mobile source set (Android + iOS) for RevenueCat
    sourceSets {
        val mobileMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.revenuecat.kmp.core)
                implementation(libs.revenuecat.kmp.ui)
            }
        }

        androidMain.get().dependsOn(mobileMain)
        iosArm64Main.get().dependsOn(mobileMain)
        iosSimulatorArm64Main.get().dependsOn(mobileMain)

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

            // Firebase Android SDKs via BoM (non-KTX artifacts)
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.19.0"))
            implementation("com.google.firebase:firebase-crashlytics")
            implementation("com.google.firebase:firebase-analytics")
            implementation(libs.firebase.firestore)
            implementation("com.google.firebase:firebase-auth")
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
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
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

    val envFile = rootProject.file(".env")
    val envProps = Properties()
    if (envFile.exists()) {
        envFile.inputStream().use { envProps.load(it) }
    }
    val revenueCatApiKey = envProps.getProperty("REVENUECAT_GOOGLE_API_KEY") ?: ""

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.creativeblisslabs.nitnemguktasteek"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 7
        versionName = "0.1.6"
        buildConfigField("String", "REVENUECAT_GOOGLE_API_KEY", "\"$revenueCatApiKey\"")
        buildConfigField("String", "SUPPORT_EMAIL", "\"creativeblisslabs@gmail.com\"")
        buildConfigField("String", "CONTACT_EMAIL", "\"creativeblisslabs@gmail.com\"")
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

            ndk.debugSymbolLevel = "FULL"

            val keyPath = keystoreProperties.getProperty("RELEASE_KEYSTORE_PATH") ?: ""
            if (keyPath.isNotEmpty()) {
                signingConfig = signingConfigs.getByName("release")
            }
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = false
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll("-Xskip-metadata-version-check", "-Xskip-prerelease-check")
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
            packageName = "Bani: Nitnem & Steek Companion"
            packageVersion = "1.0.0"
            description = "A peaceful Nitnem companion for daily reading and listening."
            vendor = "creativeblisslabs"
        }
    }
}