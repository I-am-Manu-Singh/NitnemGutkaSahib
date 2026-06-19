# 🛕 Play Store Readiness — Nitnem Gutka Sahib
**App:** `org.audiocular.nitnem`  
**Current Version:** `0.1.0` (versionCode: 1)  
**Stack:** Kotlin Multiplatform + Compose Multiplatform  
**Assessed:** June 2026

---

## 🟢 RESOLVED BLOCKERS

These items were identified as release blockers and have been fully implemented/resolved:

### 1. Release Signing Config (RESOLVED ✅)
The app now has a release signing configuration that dynamically loads signing credentials from `local.properties` to ensure developers do not expose keystore passwords in version control.
- **Gradle File:** [`composeApp/build.gradle.kts`](file:///Users/manpreet/Desktop/AndroidStudioProjects/NitnemGuktaSteek/composeApp/build.gradle.kts)
- **Local Settings:** Handled via `RELEASE_KEYSTORE_PATH`, `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, and `RELEASE_KEY_PASSWORD` keys in local properties.

### 2. Minification & ProGuard Setup (RESOLVED ✅)
Release builds are now configured for size optimization and code obfuscation to protect Gurbani logic and companion assets.
- **Gradle Config:** `isMinifyEnabled = true` and `isShrinkResources = true` are applied.
- **Rules File:** [`composeApp/proguard-rules.pro`](file:///Users/manpreet/Desktop/AndroidStudioProjects/NitnemGuktaSteek/composeApp/proguard-rules.pro) was created, containing rules tailored for Kotlin Multiplatform, Compose Multiplatform, kotlinx.serialization, Koin, Ktor, WorkManager, and Media3.

---

## 🔴 ACTIVE BLOCKERS — Must Fix Before Upload

These remaining blockers will cause Google Play rejection or make the launch blind.

### 3. No Real Analytics Backend (Firebase / GA4)
**Files:** [`shared/analytics/AnalyticsEvent.kt`](file:///Users/manpreet/Desktop/AndroidStudioProjects/NitnemGuktaSteek/shared/analytics/AnalyticsEvent.kt), [`composeApp/src/commonMain/.../AnalyticsRepository.kt`](file:///Users/manpreet/Desktop/AndroidStudioProjects/NitnemGuktaSteek/composeApp/src/commonMain/kotlin/org/audiocular/nitnem/data/analytics/AnalyticsRepository.kt)

The app tracks user stats locally in DataStore (which powers the streak companion features), but lacks any external analytics server. You will have no user engagement metrics or active user counts.

**Fix — Integrate Firebase Analytics:**
1. Create a project at the [Firebase Console](https://console.firebase.google.com).
2. Register an Android app using package `org.audiocular.nitnem`.
3. Download `google-services.json` and place it in the `composeApp/` directory.
4. Add the Google Services plugin to `build.gradle.kts` and dependencies:
   ```kotlin
   implementation("com.google.firebase:firebase-analytics-ktx:21.5.0")
   ```

### 4. Launcher Icon Missing MDPI/XHDPI/XXHDPI/XXXHDPI Density Variants
**Dir:** `composeApp/src/androidMain/res/`

The project has adaptive launcher XML drawables in `mipmap-anydpi-v26` and `mipmap-anydpi`, but the individual density folders (e.g. `mipmap-hdpi`, `mipmap-xhdpi`, etc.) are empty. Legacy devices and standard compilation packages require physical PNG fallbacks in these folders.

**Fix:**
Create the standard density PNG assets using Android Studio's **Image Asset Studio** (Right-click `res` folder → `New` → `Image Asset`) to generate all densities from your source logo.

---

## 📋 Play Store Console Submission details

Google Play Console requires extensive metadata and settings. Make sure you compile the following fields accurately:

### 1. Store Listing Details
* **App Name:** `Nitnem Gutka Sahib` (Maximum 50 characters)
* **Short Description:** `Peaceful Nitnem companion with audio recitation, translations, and daily streaks.` (Maximum 80 characters)
* **Full Description:** (Maximum 4,000 characters)
  Describe the key features:
  - Daily Nitnem Banis (Japji Sahib, Jaap Sahib, Tav-Prasad Savaiye, Chaupai Sahib, Anand Sahib, Rehras Sahib, Kirtan Sohila, and more).
  - High-quality audio playback with synchronized line scrolling.
  - Multi-language translation & transliteration support (English, Punjabi, Hindi).
  - Gamification & Progression (streak tracking, companion mascot state).
  - Interactive Gurbani Quiz & AI Assistant tab for exploration.
  - Offline-first accessibility (audio can be downloaded and played offline).

### 2. Graphic Assets
* **App Icon:** `512 x 512` pixels PNG format, maximum 1MB size, transparent background.
* **Feature Graphic:** `1024 x 500` pixels PNG format, representing the tranquil and spiritual nature of the companion.
* **Phone Screenshots:** At least 2 screenshots of the app, `16:9` or `9:16` aspect ratio, minimum 320px up to 3840px dimension. Ensure they showcase:
  - Home screen layout (showing streak progression / mascot).
  - Gurbani Reader tab (highlighting translation and font configurations).
  - Audio Recitation player view (mini player or full player).
  - AI Assistant / Quiz tabs.

### 3. Categories & Content Rating
* **Application Type:** Apps
* **Category:** Books & Reference (alternative: Education)
* **Target Audience:** All ages (Everyone / 3+). You must complete the Google Play target audience checklist confirming there is no content inappropriate for children.
* **IARC Content Rating Questionnaire:**
  - Select "Reference, News, or Educational" category.
  - Answer "No" to questions regarding violence, crude content, or user-to-user communications.

---

## 📊 Analytics Setup & Data Safety Declaration

Google Play Console requires you to fill out the **Data Safety Form** declaring what user data is processed.

### 1. Data Safety Declaration
Since the app's current statistics (completed Banis, streaks, reading/listening minutes, bookmarks) are **stored locally on-device** via DataStore:
- **Is user data collected or shared?** Select **No** (unless you integrate the Firebase SDK).
- If you add Firebase Analytics, you must check **Yes** for:
  - **App functionality & Analytics:** App interactions (screens viewed, buttons tapped) and Device IDs.
  - State that this data is **encrypted in transit** and that users can request their data to be deleted.

### 2. Analytics Event Architecture
If you eventually connect your local `AnalyticsRepository` to a server, map these existing local events to the analytics SDK:

| Local Event (`AnalyticsEvent`) | Recommended Firebase Event Name | Event Parameters |
|---|---|---|
| `AppOpened` | `app_open` | `timestamp` |
| `BaniCompleted` | `bani_completed` | `bani_id`, `reading_minutes` |
| `PlaybackStarted` | `playback_started` | `bani_id` |
| `PlaybackPaused` | `playback_paused` | `bani_id`, `position_ms` |
| `BookmarkAdded` | `bookmark_added` | `line_id` |

---

## 🏁 Readiness Summary

| Category | Status | Priority | Notes |
|----------|--------|----------|-------|
| **Release Signing** | ✅ Resolved | — | Dynamically configured via properties |
| **Minification & ProGuard** | ✅ Resolved | — | Configured in Gradle & rules file |
| **Target SDK Version** | ✅ Resolved | — | Target SDK set to 35 |
| **Launcher Densities** | ⚠️ Incomplete | 🔴 Blocker | Needs physical PNG fallbacks |
| **Analytics Backend** | ⚠️ Incomplete | 🔴 Blocker | Currently local-only |
| **Privacy Policy** | ❌ Missing | 🟡 High | Required URL for Play Console |
| **Crash Reporting** | ❌ Missing | 🟡 High | Highly recommended for production |
| **Store Visual Assets** | ❌ Missing | 🟡 High | Must compile screenshots and graphics |