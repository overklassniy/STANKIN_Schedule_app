# Module ui/schedule-widget (`:ui:schedule-widget`)

UI module for the schedule widget. Contains widget configuration screen and Glance widget for displaying schedule on the home screen, with updates via WorkManager.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/widget/ui/
  components/                  - Configuration screen components
  configure/                   - Configuration Activity and ViewModel
  glance/                      - Glance widget and its components
  utils/                       - Utilities (deep link)
```

## Key components

- `components/ScheduleWidgetConfigureScreen` - widget configuration Compose screen
- `components/ScheduleWidgetConfigureAppBar` - widget configuration toolbar
- `configure/ScheduleWidgetConfigureActivity` - widget configuration Activity
- `configure/ScheduleWidgetConfigureViewModel` - configuration ViewModel
- `glance/ScheduleGlanceWidget` - schedule Glance widget
- `glance/ScheduleGlanceWidgetReceiver` - widget receiver
- `glance/ScheduleGlanceEntryPoint` - Glance entry point
- `glance/ScheduleWidgetUpdater` - widget updater
- `glance/WidgetStateKeys` - widget state keys
- `utils/ScheduleDeepLink` - deep link for opening schedule from widget

## Dependencies

- `:ui:core`
- `:ui:schedule-core`
- `:data:core`
- `:data:schedule-widget`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-widget`
- `:domain:schedule-settings`

## Technologies

- Jetpack Compose, Material 3
- Glance (widget)
- Hilt (DI)