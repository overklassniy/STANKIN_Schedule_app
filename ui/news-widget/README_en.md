# Module ui/news-widget (`:ui:news-widget`)

UI module for the news widget. Contains widget configuration screen and Glance widget for displaying news on the home screen, with updates via WorkManager.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/news/widget/ui/
  components/                  - Configuration screen components
  configure/                   - Configuration Activity and ViewModel
  glance/                      - Glance widget and its components
  worker/                      - Update Worker and scheduler
```

## Key components

- `components/NewsWidgetConfigureScreen` - widget configuration Compose screen
- `components/NewsWidgetConfigureAppBar` - widget configuration toolbar
- `configure/NewsWidgetConfigureActivity` - widget configuration Activity
- `configure/NewsWidgetConfigureViewModel` - configuration ViewModel
- `glance/NewsGlanceWidget` - news Glance widget
- `glance/NewsGlanceWidgetReceiver` - widget receiver
- `glance/NewsGlanceEntryPoint` - Glance entry point
- `glance/NewsWidgetUpdater` - widget updater
- `glance/NewsWidgetStateKeys` - widget state keys
- `worker/NewsWidgetUpdateWorker` - widget update Worker
- `worker/NewsWidgetWorkerScheduler` - update scheduler

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:news-core`
- `:domain:news-widget`

## Technologies

- Jetpack Compose, Material 3
- Glance (widget)
- WorkManager (widget updates)
- Hilt (DI)