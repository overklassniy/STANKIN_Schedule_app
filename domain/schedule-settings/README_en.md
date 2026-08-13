# Module domain/schedule-settings (`:domain:schedule-settings`)

Domain layer for schedule display settings. Contains pair color models, preferences repository interface and use case.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/settings/domain/
  model/                       - Settings models
  repository/                  - Preferences repository interface
  usecase/                     - Settings use case
```

## Key components

### model
- `PairColorGroup` - color by pair group
- `PairColorType` - color by pair type

### repository
- `SchedulePreference` - schedule display preferences repository interface

### usecase
- `ScheduleSettingsUseCase` - schedule display settings use case

## Dependencies

- `:domain:core`

## Technologies

- Kotlin Coroutines
- Hilt (DI)