# Модуль domain/schedule-settings (`:domain:schedule-settings`)

Domain-слой настроек отображения расписания. Содержит модели цветов по типам пар, интерфейс репозитория настроек и use case.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/settings/domain/
  model/                       - Модели настроек
  repository/                  - Интерфейс репозитория настроек
  usecase/                     - Use case настроек
```

## Ключевые компоненты

### model
- `PairColorGroup` - цвет по группе пары
- `PairColorType` - цвет по типу пары

### repository
- `SchedulePreference` - интерфейс репозитория настроек отображения расписания

### usecase
- `ScheduleSettingsUseCase` - use case настроек отображения расписания

## Зависимости

- `:domain:core`

## Технологии

- Kotlin Coroutines
- Hilt (DI)