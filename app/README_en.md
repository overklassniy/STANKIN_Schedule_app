# Module app (`:app`)

Main application module, entry point. Contains `MainActivity`, `MainApplication`, screen navigation, DI configuration, Firebase and Sentry integrations.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/
  MainActivity.kt              - Main Activity, host for Compose navigation
  MainApplication.kt           - Application class, Hilt/Sentry/Firebase initialization
  data/                        - Analytics and crash reporting implementations
  di/                          - App DI module (AppModule)
  navigation/                  - Navigation graph, bottom bar, entry points
  ui/                          - Main screen and navigation bar
```

## Key components

### data
- `FirebaseProductAnalytics` - product analytics implementation via Firebase
- `SentryCrashReporter` - crash reporting implementation via Sentry

### di
- `AppModule` - Hilt module providing analytics and crash reporter implementations

### navigation
- `AppNavigation` - main application navigation graph
- `ModuleJournalNavigation` - navigation to module journal screens
- `ScheduleNavigation` - navigation to schedule screens
- `entry/BottomNavEntry` - bottom navigation bar item
- `entry/DestinationNavEntry` - navigation entry to a specific screen
- `entry/NavigationEntry` - base navigation entry model

### ui
- `MainScreen` - root screen with bottom navigation bar
- `AppNavigationBar` - bottom navigation bar component

## Dependencies

This module depends on all project modules:

- **data:** `:data:core`, `:data:journal-core`, `:data:news-core`, `:data:news-widget`, `:data:schedule-core`, `:data:schedule-ical`, `:data:schedule-table`, `:data:schedule-parser`, `:data:schedule-repository`, `:data:schedule-settings`, `:data:schedule-viewer`, `:data:schedule-widget`
- **domain:** `:domain:core`, `:domain:news-core`, `:domain:news-widget`, `:domain:schedule-core`, `:domain:schedule-settings`
- **ui:** `:ui:core`, `:ui:home`, `:ui:journal-login`, `:ui:journal-predict`, `:ui:journal-viewer`, `:ui:schedule-creator`, `:ui:schedule-editor`, `:ui:schedule-table`, `:ui:schedule-parser`, `:ui:schedule-list`, `:ui:schedule-repository`, `:ui:schedule-viewer`, `:ui:schedule-widget`, `:ui:news-review`, `:ui:news-widget`, `:ui:settings`

## Technologies

- Jetpack Compose, Material 3
- Navigation Compose
- Hilt (DI)
- Firebase Analytics
- Sentry (crash reporting)
- Room (DB schemas)
- WorkManager (background tasks)
- In-App Update