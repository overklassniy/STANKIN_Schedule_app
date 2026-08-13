# Модуль ui/schedule-table (`:ui:schedule-table`)

UI-модуль таблицы расписания. Содержит экран таблицы расписания с поддержкой масштабирования, настройками отображения и экспортом в различные форматы.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/table/ui/
  ScheduleTableScreen.kt       - Compose-экран таблицы
  ScheduleTableViewActivity.kt - Activity просмотра таблицы
  ScheduleTableViewModel.kt    - ViewModel таблицы
  components/                  - Компоненты (тулбар, экспорт, настройки)
```

## Ключевые компоненты

- `ScheduleTableScreen` - Compose-экран таблицы расписания
- `ScheduleTableViewActivity` - Activity просмотра таблицы
- `ScheduleTableViewModel` - ViewModel таблицы
- `components/TableView` - представление таблицы
- `components/ZoomableBox` - контейнер с поддержкой масштабирования
- `components/ScheduleTableAppBar` - верхний тулбар
- `components/ScheduleTableBottomAppBar` - нижний тулбар
- `components/SettingsSheet` - bottom sheet настроек таблицы
- `components/TableFomatDialog` - диалог формата таблицы
- `components/ExportFormat` - формат экспорта
- `components/ExportType` - тип экспорта
- `components/ExportProgress` - индикатор прогресса экспорта
- `components/ExportSnackbar` - snackbar экспорта

## Зависимости

- `:ui:core`
- `:ui:schedule-core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-table`
- `:domain:schedule-settings`

## Технологии

- Jetpack Compose, Material 3
- Hilt (DI)