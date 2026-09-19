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

# Keep our models, domains, and security utilities intact
-keep class com.creativeblisslabs.nitnemguktasteek.models.** { *; }
-keep class com.creativeblisslabs.nitnemguktasteek.domain.** { *; }
-keep class com.creativeblisslabs.nitnemguktasteek.data.** { *; }
-keep class com.creativeblisslabs.nitnemguktasteek.security.** { *; }
-keep class org.audiocular.nitnem.** { *; }

# Firebase, Google Play Services, Firebase App Check & Play Integrity API
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.firebase.appcheck.** { *; }
-dontwarn com.google.firebase.appcheck.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.android.play.core.integrity.** { *; }
-dontwarn com.google.android.play.core.integrity.**

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

# ==============================================================================
# STRIP LOG STATEMENTS IN RELEASE BUILDS
# (Runs only in release builds where minification & R8 optimization are enabled.
#  Debug builds remain unaffected so logs continue to work normally during testing.)
# ==============================================================================
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int println(...);
}

-assumenosideeffects class java.io.PrintStream {
    public void println(java.lang.String);
    public void println(java.lang.Object);
    public void println(boolean);
    public void println(char);
    public void println(char[]);
    public void println(double);
    public void println(float);
    public void println(int);
    public void println(long);
    public void print(java.lang.String);
    public void print(java.lang.Object);
    public void print(boolean);
    public void print(char);
    public void print(char[]);
    public void print(double);
    public void print(float);
    public void print(int);
    public void print(long);
}

