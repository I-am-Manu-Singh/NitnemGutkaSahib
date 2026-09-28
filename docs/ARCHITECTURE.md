# Architecture

## Direction

Nitnem Steek is Android-first with shared Kotlin Multiplatform modules prepared for iOS. The first implementation establishes the module graph, domain contracts, offline-first repository shape, and a Compose Multiplatform UI shell.

## Layers

- UI: `composeApp`
- Domain contracts and use cases: `shared:domain`
- Data implementation: `shared:data`
- Shared models: `shared:models`
- Feature engines: `shared:audio-sync`, `shared:streak`, `shared:reminders`, `shared:analytics`, `shared:gamification`

## Offline-First Plan

Current state uses an in-memory offline-first repository seeded with sample content. The intended production storage path is:

- Room for Android app-local persistence.
- SQLDelight or another KMP-compatible database for shared persistence when iOS is enabled.
- DataStore for preferences such as script mode, font scale, reminder settings, accessibility mode, and last playback position.
- Ktor plus Kotlin Serialization for reviewed content sync.

## Feature Contracts

- Reading: bani list, line model, multi-script display, focus-mode-ready layout.
- Audio: timestamped line model and `AudioLineSynchronizer` for highlighting and repeat pauri.
- Streaks: compassionate streak progression with recovery language and freeze support.
- Reminders: gentle reminder defaults and message policy.
- Gamification: companion/environment progression without casino mechanics or competitive pressure.
- Analytics: no-op tracker contract so privacy rules can be enforced before any provider is added.

## Guardrails

- Do not generate Gurbani text inside AI features.
- Keep interpretations clearly educational and non-authoritative.
- Avoid public leaderboards, follower counts, and scarcity loops.
- Accessibility and elder-friendly typography are core requirements, not polish tasks.

## Next Build Milestones

1. Replace sample bani placeholders with reviewed local content assets and import tooling.
2. Add Room/DataStore on Android and connect the repository to persistent state.
3. Add Media3 playback service, notification controls, audio focus, and lockscreen support.
4. Add real synchronized audio timestamp files and offline audio download management.
5. Split UI tabs into feature packages once screens grow beyond the current shell.
6. Add tests for streak recovery, reminder planning, audio line lookup, and repository state.
