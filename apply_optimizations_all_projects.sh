#!/usr/bin/env bash
set -e

echo "================================================================="
echo "   Applying ProGuard/R8, Log-Stripping & Security Optimizations  "
echo "================================================================="

PROGUARD_RULES_TEMPLATE='
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

# Keep Kotlin Multiplatform & Compose attributes
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Serialization
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# Keep Firebase, Google Play & Play Integrity
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.android.play.core.integrity.** { *; }
-dontwarn com.google.android.play.core.integrity.**
'

# 1. Process Android Studio Projects
ANDROID_STUDIO_ROOT="/Users/manpreet/Desktop/AndroidStudioProjects"
if [ -d "$ANDROID_STUDIO_ROOT" ]; then
    echo "Processing Android Studio Projects in: $ANDROID_STUDIO_ROOT"
    for proj in "$ANDROID_STUDIO_ROOT"/*; do
        if [ -d "$proj" ]; then
            proj_name=$(basename "$proj")
            echo "-> Inspecting Android project: $proj_name"
            
            # Find all proguard-rules.pro files or potential app modules
            for sub in "app" "composeApp" "androidApp" "mobile" "wear"; do
                target_dir="$proj/$sub"
                if [ -d "$target_dir" ]; then
                    p_file="$target_dir/proguard-rules.pro"
                    if [ ! -f "$p_file" ]; then
                        echo "   [+] Creating $p_file"
                        echo "$PROGUARD_RULES_TEMPLATE" > "$p_file"
                    else
                        if ! grep -q "assumenosideeffects class android.util.Log" "$p_file"; then
                            echo "   [+] Appending log-stripping rules to $p_file"
                            echo "$PROGUARD_RULES_TEMPLATE" >> "$p_file"
                        else
                            echo "   [✓] Already optimized: $p_file"
                        fi
                    fi
                fi
            done
        fi
    done
fi

# 2. Process Flutter Projects
FLUTTER_ROOT="/Users/manpreet/Desktop/FlutterProjects"
if [ -d "$FLUTTER_ROOT" ]; then
    echo ""
    echo "Processing Flutter Projects in: $FLUTTER_ROOT"
    for proj in "$FLUTTER_ROOT"/*; do
        if [ -d "$proj" ]; then
            proj_name=$(basename "$proj")
            echo "-> Inspecting Flutter project: $proj_name"
            android_app_dir="$proj/android/app"
            if [ -d "$android_app_dir" ]; then
                p_file="$android_app_dir/proguard-rules.pro"
                if [ ! -f "$p_file" ]; then
                    echo "   [+] Creating $p_file"
                    echo "$PROGUARD_RULES_TEMPLATE" > "$p_file"
                else
                    if ! grep -q "assumenosideeffects class android.util.Log" "$p_file"; then
                        echo "   [+] Appending log-stripping rules to $p_file"
                        echo "$PROGUARD_RULES_TEMPLATE" >> "$p_file"
                    else
                        echo "   [✓] Already optimized: $p_file"
                    fi
                fi
            fi
        fi
    done
fi

# 3. Process React Native Projects
RN_ROOT="/Users/manpreet/Desktop/ReactNativeProjects"
if [ -d "$RN_ROOT" ]; then
    echo ""
    echo "Processing React Native Projects in: $RN_ROOT"
    for proj in "$RN_ROOT"/*; do
        if [ -d "$proj" ]; then
            proj_name=$(basename "$proj")
            echo "-> Inspecting React Native project: $proj_name"
            android_app_dir="$proj/android/app"
            if [ -d "$android_app_dir" ]; then
                p_file="$android_app_dir/proguard-rules.pro"
                if [ ! -f "$p_file" ]; then
                    echo "   [+] Creating $p_file"
                    echo "$PROGUARD_RULES_TEMPLATE" > "$p_file"
                else
                    if ! grep -q "assumenosideeffects class android.util.Log" "$p_file"; then
                        echo "   [+] Appending log-stripping rules to $p_file"
                        echo "$PROGUARD_RULES_TEMPLATE" >> "$p_file"
                    else
                        echo "   [✓] Already optimized: $p_file"
                    fi
                fi
            fi
        fi
    done
fi

echo ""
echo "================================================================="
echo "   Optimization Pass Complete for all Project Groups!            "
echo "================================================================="
