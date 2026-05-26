import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {

    alias(libs.plugins.kotlinMultiplatform)

    alias(libs.plugins.androidApplication)

    alias(libs.plugins.composeMultiplatform)

    alias(libs.plugins.composeCompiler)

    kotlin("plugin.serialization")
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
            implementation(libs.media3.exoplayer)
            implementation("androidx.media3:media3-session:1.4.1")
            implementation("androidx.media3:media3-ui:1.4.1")
            implementation("androidx.work:work-runtime-ktx:2.9.0")
            implementation("androidx.glance:glance-appwidget:1.1.1")
            implementation("androidx.glance:glance-material3:1.1.1")
            implementation("androidx.datastore:datastore-preferences:1.1.1")
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
    namespace = "org.audiocular.nitnem"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.audiocular.nitnem"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.audiocular.nitnem.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "Nitnem Gutka Steek"
            packageVersion = "1.0.0"
            description = "A peaceful Nitnem companion for daily reading and listening."
            vendor = "Audiocular"
        }
    }
}
