# Nitnem Gutka Steek 🌅

A premium, offline-first Kotlin Multiplatform (KMP) and Compose Multiplatform companion app designed to facilitate a tranquil and immersive experience for reading, listening, and learning daily Nitnem prayers (Banis). 

---

## 🌟 Key Features

- **Rich Premium Visuals**: Beautiful glassmorphic components, tailored HSL color palettes, and ambient backdrops matching the active prayer.
- **Dynamic Background Canvas Animations**:
  - **HeroCard Background**: Parallax drifting clouds (moving across the entire width of the card), glowing 4-pointed twinkling stars, and dual gently-swaying floating ambient icons.
  - **Consistency Card (Plant Growth)**: Displays a swaying, growing plant stem with leaves that branch off relative to the user's daily habits.
  - **Listening Card (Water Wave)**: Renders a shifting double sine-wave water filling effect that represents daily listening durations.
  - **Today Card (Rising Bubbles)**: Renders floating outline circles that drift upwards and fade out based on completed daily prayers.
- **ExoPlayer Caching & Download Engine**: Media3 streaming audio player configured with an automatic 100MB Least Recently Used (LRU) local disk cache to minimize mobile data usage. Supports downloading default recitations for full offline playback.
- **Sync Studio & Speed Controller**: Integrated Sync Editor permitting users to edit line-by-line audio-text alignment. Includes a playback speed adjuster selector (`0.5x`, `0.75x`, `1.0x`, `1.25x`, `1.5x`).
- **Flexible Reminders & Notifications**: Set custom schedules for Amrit Vela, Rehras Sahib, and Kirtan Sohila. Saves schedules persistently using standard 12-hour AM/PM formatting.
- **Daily Live Hukamnama**: Downloads the daily verse, supporting Larivaar (continuous word spacing) mode and dynamic translations for writer, raag, and content chips.
- **Multilingual Support**: Supports English, Punjabi, and Hindi, dynamic settings labels, status headers, and prayer cards translation.

---

## 🏗️ Architecture & Modules

The project is architected using Clean Architecture principles, ensuring that UI shells and platform dependencies are decoupled from core business policies.

```
                  ┌──────────────────────┐
                  │      composeApp      │ (Shared Compose UI & Shell)
                  └──────────┬───────────┘
                             │
       ┌─────────────────────┼─────────────────────┐
       ▼                     ▼                     ▼
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│ shared:data │       │shared:streak│       │ shared:rem  │ ...
└──────┬──────┘       └──────┬──────┘       └──────┬──────┘
       │                     │                     │
       └─────────────────────┼─────────────────────┘
                             ▼
                      ┌──────────────┐
                      │shared:models │ (Plain Serializable Models)
                      └──────────────┘
```

### Module breakdown:
- **`composeApp`**: Main application code, containing Android/iOS launchers, UI screens, widgets, assets, ViewModels, and the audio controller wrappers.
- **`shared:models`**: Plain serializable data types, models (e.g., `Bani`, `BaniLine`), and theme settings.
- **`shared:data`**: Offline-first repository implementations, JSON databases, and default ragi/recitation catalogs.
- **`shared:models`**: Serializable domain models and value objects.
- **`shared:streak`**: Policies and math rules to calculate compassionate habit streaks.
- **`shared:reminders`**: Local notification scheduling criteria.
- **`shared:gamification`**: Companion progress rules and milestones.
- **`shared:audio-sync`**: Audio-text word alignment structures.
- **`shared:analytics`**: Privacy-centric event logging interface.

---

## 📂 Core File References

- **[HomeScreen.kt](file:///Users/manpreet/Desktop/AndroidStudioProjects/NitnemGuktaSteek/composeApp/src/commonMain/kotlin/org/audiocular/nitnem/app/ui/screens/HomeScreen.kt)**: Main screen displaying user progress, streaks, daily analytics cards, and lists of Banis.
- **[HeroCard.kt](file:///Users/manpreet/Desktop/AndroidStudioProjects/NitnemGuktaSteek/composeApp/src/commonMain/kotlin/org/audiocular/nitnem/app/ui/components/HeroCard.kt)**: Header card recommending current prayers, showing daily Hukamnama detail sheets, and drawing the twinkling star and drifting cloud canvas.
- **[SettingsScreen.kt](file:///Users/manpreet/Desktop/AndroidStudioProjects/NitnemGuktaSteek/composeApp/src/commonMain/kotlin/org/audiocular/nitnem/app/ui/screens/SettingsScreen.kt)**: Consolidated card dashboard managing user configuration, dark/AMOLED modes, accent theme selectors, and custom audio uploads.
- **[SyncEditorScreen.kt](file:///Users/manpreet/Desktop/AndroidStudioProjects/NitnemGuktaSteek/composeApp/src/commonMain/kotlin/org/audiocular/nitnem/app/sync/SyncEditorScreen.kt)**: Composable allowing alignment editing, play/pause controls, progress dragging, and speed selectors.
- **[AndroidStreamAudioController.kt](file:///Users/manpreet/Desktop/AndroidStudioProjects/NitnemGuktaSteek/composeApp/src/androidMain/kotlin/org/audiocular/nitnem/audio/AndroidStreamAudioController.kt)**: Standard Android ExoPlayer controller that initializes `SimpleCache` and LRU cache sources.
- **[Translations.kt](file:///Users/manpreet/Desktop/AndroidStudioProjects/NitnemGuktaSteek/composeApp/src/commonMain/kotlin/org/audiocular/nitnem/utils/Translations.kt)**: Dynamic translation mapping dictionaries and Raag/Writer normalization functions.

---

## 🛠️ Build & Compilation

To build the debug application bundle:

```bash
./gradlew :composeApp:assembleDebug
```

To compile Kotlin Multiplatform sources:

```bash
./gradlew :composeApp:compileDebugKotlin
```

---

## 🕊️ Product Principles

- **Compassionate Design**: Focus on encouraging habits without relying on stress-inducing gamification tricks.
- **Full Accessibility**: Respects user fonts, support large sizes (Elder mode), and high-contrast color choices.
- **Spiritual Devotion**: Maintain a calm visual environment respecting the sacred nature of the Gurbani.
