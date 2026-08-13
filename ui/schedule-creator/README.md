# Модуль ui/schedule-creator (`:ui:schedule-creator`)

UI-модуль создания расписания. Содержит bottom sheet для создания нового расписания или импорта существующего, с обработкой разрешений на чтение файлов.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/creator/ui/
  ScheduleCreatorSheet.kt      - Bottom sheet создания расписания
  ScheduleCreatorViewModel.kt  - ViewModel создания
  components/                  - Компоненты (создание, импорт, диалоги)
```

## Ключевые компоненты

- `ScheduleCreatorSheet` - bottom sheet для создания/импорта расписания
- `ScheduleCreatorViewModel` - ViewModel управления созданием
- `components/CreateEvent` - событие создания расписания
- `components/CreateState` - состояние создания
- `components/ImportEvent` - событие импорта расписания
- `components/ImportState` - состояние импорта
- `components/ReadPermissionDeniedDialog` - диалог отказа в разрешении
- `components/ScheduleCreateDialog` - диалог создания расписания

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`

## Технологии

- Jetpack Compose, Material 3
- Hilt (DI)