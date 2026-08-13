# Module domain/schedule-core (`:domain:schedule-core`)

Domain layer for schedules. Contains core domain models (pair, schedule, date, time, types), repository interfaces (storage, device) and use cases for working with pairs and schedules.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/core/domain/
  exceptions/                  - Schedule exceptions
  model/                       - Domain models
  repository/                  - Repository interfaces
  usecase/                     - Schedule use cases
```

## Key components

### exceptions
- `ScheduleException` - schedule exceptions

### model
- `PairModel` - pair (class) model
- `PairInfo` - pair info
- `ScheduleModel` - schedule model
- `ScheduleInfo` - schedule info
- `ScheduleDayModel` - schedule day model
- `DateModel`, `DateSingle`, `DateRange`, `DateItem` - date models
- `DayOfWeek` - day of week
- `Time` - pair time
- `Type` - class type (lecture, practice, lab)
- `Subgroup` - subgroup
- `Frequency` - frequency

### repository
- `ScheduleStorage` - schedule storage interface
- `ScheduleDeviceRepository` - device repository interface

### usecase
- `ScheduleUseCase` - schedule use case
- `PairUseCase` - pair use case
- `ScheduleDeviceUseCase` - device use case

## Dependencies

- `:domain:core`

## Technologies

- Kotlin Coroutines
- Paging (pagination)
- Hilt (DI)