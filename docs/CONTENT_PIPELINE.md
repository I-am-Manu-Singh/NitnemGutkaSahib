# Content Pipeline

## Where Audio Files Go

Use this structure during development:

- Prototype bundled audio: `composeApp/src/commonMain/composeResources/files/content/audio/`
- Prototype bani text: `composeApp/src/commonMain/composeResources/files/content/banis/`
- Android production downloads: app-specific storage managed by Media3 DownloadManager.
- iOS production downloads: app container storage managed by AVFoundation.
- macOS production downloads: app support directory.

Bundle only short prototype clips. Full Nitnem audio should be downloaded, cached, and removable by the user so the app stays small.

## Text Files Required For Playback

Every audio file needs a reviewed timing sidecar file:

- `japji-sahib.mp3`
- `japji-sahib.timing.json`

The timing file maps each bani line to `startsAtMs` and `endsAtMs`. The reader should render from the text JSON, while the audio player highlights using the timing JSON.

## Language Text

Store each line with:

- `gurmukhi`: original Gurmukhi.
- `transliteration`: Roman transliteration.
- `english`: English transliteration or interpretation.
- `hindi`: Hindi Transliteration or interpretation.
- `punjabi`: Punjabi translation or teeka.
- `wordMeaning`: reviewed word-by-word meaning.

Never generate Gurbani text with AI. AI can explain reviewed source text, but it must not invent lines, translations, or attributions.

## Recommended Sources To Review

- [BaniDB/Khalis Foundation](https://www.banidb.com/) for Gurbani database/API integration. Their terms say BaniDB API data is subject to NPOSL-3.0 and has project-use requirements, so review the terms before integrating.
- [SikhiToTheMax](https://www.banidb.com/) through the BaniDB alliance for Gurmukhi, transliteration, translations, and Punjabi resources.
- [Shabad OS database](https://github.com/shabados/database) for open database tooling and licensing review. Their package notes separate Gurbani/public-domain-mark material from translations/transliterations and other supporting text.
- [SikhNet Play/Gurbani Media Center](https://www.sikhnet.com/apps?from=AppAgg.com) for audio discovery and licensing conversations. SikhNet describes it as a large Gurbani audio archive, but that does not automatically mean tracks can be redistributed in this app.

Before shipping, confirm license terms for every translation, teeka, audio track, and timing file. If licensing is unclear, request written permission or use only internally reviewed/public-domain material.

## Apple Notifications And Widgets

Compose shared code should own reminder rules and widget state. Native shells should render them:

- iOS notifications: `UNUserNotificationCenter`.
- iOS widgets: WidgetKit extension.
- macOS notifications: UserNotifications framework.
- macOS widgets: WidgetKit extension for macOS.

The KMP shared modules should expose the same reminder and journey state to Android, iOS, and macOS so behavior stays consistent.
