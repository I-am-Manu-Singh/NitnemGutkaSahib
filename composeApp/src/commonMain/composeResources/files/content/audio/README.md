# Audio Assets

For prototype-only bundled audio, place short reviewed audio files here.

For production, prefer downloaded/offline-managed files outside the app bundle:

- Android: app-specific storage via Media3 download manager.
- iOS: app container documents/cache directory via AVFoundation.
- macOS: app support directory.

Keep a sidecar timing file beside each audio track:

- `japji-sahib.mp3`
- `japji-sahib.timing.json`

The timing JSON should map line IDs to `startsAtMs` and `endsAtMs`.
