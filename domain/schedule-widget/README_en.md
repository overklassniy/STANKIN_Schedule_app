# Module domain/schedule-widget (`:domain:schedule-widget`)

Domain layer for the schedule widget. Contains widget domain models (data, day, pair, type), preferences repository interface and use cases for widget configuration and update.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/widget/domain/
  model/                       - Schedule widget models
  repository/                  - Preferences repository interface
  usecase/                     - Widget use cases
```

## Key components

### model
- `ScheduleWidgetData` - schedule widget data
- `ScheduleWidgetDay` - day in widget
- `ScheduleWidgetPair` - pair in widget
- `ScheduleWidgetPairType` - pair type in widget
- `ScheduleItem` - schedule item

### repository
- `ScheduleWidgetPreference` - widget preferences repository interface

### usecase
- `ScheduleConfigureUseCase` - widget configuration use case
- `ScheduleWidgetUseCase` - widget update use case

## Dependencies

- `:domain:core`
- `:domain:schedule-core`

## Technologies

- Kotlin Coroutines
- Hilt (DI)