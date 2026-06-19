# ProGuard / R8 optimization and obfuscation rules for Nitnem Gutka Sahib

# Keep all Kotlin Multiplatform and Jetpack Compose/Compose Multiplatform components
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Compose Compiler / Runtime internal tags & structures
-keep class androidx.compose.runtime.ParcelableSnapshotMutableState { *; }
-keep class androidx.compose.runtime.snapshots.SnapshotStateList { *; }

# Keep Koin dependencies and components
-keep class io.insertkoin.** { *; }
-dontwarn io.insertkoin.**

# Keep kotlinx.serialization classes & custom serialized classes
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# Keep our models and domains intact
-keep class org.audiocular.nitnem.** { *; }

# AndroidX / Media3 Exoplayer / Glance Widgets
-keep class androidx.media3.exoplayer.** { *; }
-keep class androidx.media3.session.** { *; }
-dontwarn androidx.media3.**
-keep class androidx.glance.** { *; }
-dontwarn androidx.glance.**
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# Ktor HTTP client
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-dontwarn io.netty.**
-dontwarn org.slf4j.**
