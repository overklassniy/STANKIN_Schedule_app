# Модуль domain/schedule-widget (`:domain:schedule-widget`)

Domain-слой виджета расписания. Содержит доменные модели виджета (данные, день, пара, тип), интерфейс репозитория настроек и use case для конфигурации и обновления виджета.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/widget/domain/
  model/                       - Модели виджета расписания
  repository/                  - Интерфейс репозитория настроек
  usecase/                     - Use case виджета
```

## Ключевые компоненты

### model
- `ScheduleWidgetData` - данные виджета расписания
- `ScheduleWidgetDay` - день в виджете
- `ScheduleWidgetPair` - пара в виджете
- `ScheduleWidgetPairType` - тип пары в виджете
- `ScheduleItem` - элемент расписания

### repository
- `ScheduleWidgetPreference` - интерфейс репозитория настроек виджета

### usecase
- `ScheduleConfigureUseCase` - use case конфигурации виджета
- `ScheduleWidgetUseCase` - use case обновления виджета

## Зависимости

- `:domain:core`
- `:domain:schedule-core`

## Технологии

- Kotlin Coroutines
- Hilt (DI)