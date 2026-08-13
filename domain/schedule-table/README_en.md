# Module domain/schedule-table (`:domain:schedule-table`)

Domain layer for schedule tables. Contains table domain models (cell, day, configuration), interfaces for table creation and export, use cases.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/table/domain/
  model/                       - Schedule table models
  repository/                  - Provider and creator interfaces
  usecase/                     - Table use cases
```

## Key components

### model
- `ScheduleTable` - schedule table model
- `ScheduleTableCell` - table cell
- `ScheduleTableDay` - day in table
- `TableConfig` - table configuration
- `TableMode` - table display mode
- `DrawScheduleTable` - table drawing model
- `DrawScheduleTableCell` - drawing cell
- `DrawExtentions` - drawing extensions

### repository
- `AndroidPublicProvider` - public storage access interface
- `AndroidTableCreator` - table creation interface

### usecase
- `AndroidTableUseCase` - schedule table use case
- `AndroidTablePainter` - table painter

## Dependencies

- `:domain:core`
- `:domain:schedule-core`

## Technologies

- Kotlin Coroutines
- Hilt (DI)