# Module domain/schedule-viewer (`:domain:schedule-viewer`)

Domain layer for schedule viewing. Contains view domain models (day, pair, content), viewer repository interface and use case.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/viewer/domain/
  model/                       - Schedule view models
  repository/                  - Viewer repository interface
  usecase/                     - Viewer use case
```

## Key components

### model
- `ScheduleViewDay` - schedule day for display
- `ScheduleViewPair` - pair for display
- `ViewContent` - base cell content
- `TextContent` - text content
- `LinkContent` - link content
- `ScheduleViewConstants` - view constants

### repository
- `ScheduleViewerRepository` - schedule viewer repository interface

### usecase
- `ScheduleViewerUseCase` - schedule viewer use case

## Dependencies

- `:domain:core`
- `:domain:schedule-core`

## Technologies

- Kotlin Coroutines
- Hilt (DI)