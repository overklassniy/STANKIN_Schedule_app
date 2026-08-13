# Модуль ui/news-widget (`:ui:news-widget`)

UI-модуль виджета новостей. Содержит экран настройки виджета и Glance-виджет для отображения новостей на домашнем экране, с обновлением через WorkManager.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/news/widget/ui/
  components/                  - Компоненты экрана настройки
  configure/                   - Activity и ViewModel настройки
  glance/                      - Glance-виджет и его компоненты
  worker/                      - Worker и планировщик обновления
```

## Ключевые компоненты

- `components/NewsWidgetConfigureScreen` - Compose-экран настройки виджета
- `components/NewsWidgetConfigureAppBar` - тулбар настройки виджета
- `configure/NewsWidgetConfigureActivity` - Activity настройки виджета
- `configure/NewsWidgetConfigureViewModel` - ViewModel настройки
- `glance/NewsGlanceWidget` - Glance-виджет новостей
- `glance/NewsGlanceWidgetReceiver` - получатель виджета
- `glance/NewsGlanceEntryPoint` - точка входа Glance
- `glance/NewsWidgetUpdater` - обновление виджета
- `glance/NewsWidgetStateKeys` - ключи состояния виджета
- `worker/NewsWidgetUpdateWorker` - Worker обновления виджета
- `worker/NewsWidgetWorkerScheduler` - планировщик обновления

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:news-core`
- `:domain:news-widget`

## Технологии

- Jetpack Compose, Material 3
- Glance (виджет)
- WorkManager (обновление виджета)
- Hilt (DI)