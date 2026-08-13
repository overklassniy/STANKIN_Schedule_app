# Модуль domain/schedule-ical (`:domain:schedule-ical`)

Domain-слой экспорта в iCal. Содержит доменные модели iCal-событий (календарь, событие, дата, повторение), интерфейс экспорта и use case.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/ical/domain/
  model/                       - Доменные модели iCal
  repository/                  - Интерфейс экспорта
  usecase/                     - Use case экспорта
```

## Ключевые компоненты

### model
- `ICalCalendar` - модель iCal-календаря
- `ICalEvent` - модель iCal-события
- `ICalDate` - базовая модель даты
- `ICalSingleDate` - одиночная дата
- `ICalRecurrenceDate` - повторяющаяся дата
- `ICalDayOfWeek` - день недели
- `ICalFrequency` - частота повторения

### repository
- `ICalExporter` - интерфейс экспорта расписания в iCal

### usecase
- `ICalExporterUseCase` - use case экспорта в iCal

## Зависимости

- `:domain:core`
- `:domain:schedule-core`

## Технологии

- Kotlin Coroutines
- Hilt (DI)