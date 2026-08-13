# Module ui/home (`:ui:home`)

UI module for the home screen. Contains root screen with bottom tab bar (schedule, news), in-app update management and tab hosting.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/home/ui/
  HomeScreen.kt                - Tabbed Compose screen
  HomeViewModel.kt             - Home screen ViewModel
  components/                  - Components (tabs, updates)
```

## Key components

- `HomeScreen` - Compose screen with schedule and news tabs
- `HomeViewModel` - home screen ViewModel
- `components/HomeTab` - home screen tab
- `components/HomeTabRow` - tab bar
- `components/InAppUpdateEvent` - in-app update event
- `components/InAppUpdateState` - in-app update state
- `components/InAppUpdateResult` - in-app update result

## Dependencies

- `:ui:core`
- `:domain:core`
- `:ui:news-review`
- `:domain:news-core`
- `:ui:news-widget`
- `:ui:schedule-core`
- `:domain:schedule-viewer`
- `:domain:schedule-settings`
- `:domain:schedule-core`

## Technologies

- Jetpack Compose, Material 3
- In-App Update (app updates)
- Hilt (DI)