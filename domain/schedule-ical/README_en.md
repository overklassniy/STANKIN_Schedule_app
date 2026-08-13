# Module domain/schedule-ical (`:domain:schedule-ical`)

Domain layer for iCal export. Contains iCal event domain models (calendar, event, date, recurrence), export interface and use case.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/ical/domain/
  model/                       - iCal domain models
  repository/                  - Export interface
  usecase/                     - Export use case
```

## Key components

### model
- `ICalCalendar` - iCal calendar model
- `ICalEvent` - iCal event model
- `ICalDate` - base date model
- `ICalSingleDate` - single date
- `ICalRecurrenceDate` - recurring date
- `ICalDayOfWeek` - day of week
- `ICalFrequency` - recurrence frequency

### repository
- `ICalExporter` - schedule export to iCal interface

### usecase
- `ICalExporterUseCase` - iCal export use case

## Dependencies

- `:domain:core`
- `:domain:schedule-core`

## Technologies

- Kotlin Coroutines
- Hilt (DI)