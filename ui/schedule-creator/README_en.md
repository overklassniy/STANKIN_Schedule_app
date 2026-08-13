# Module ui/schedule-creator (`:ui:schedule-creator`)

UI module for schedule creation. Contains bottom sheet for creating a new schedule or importing an existing one, with file read permission handling.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/creator/ui/
  ScheduleCreatorSheet.kt      - Schedule creation bottom sheet
  ScheduleCreatorViewModel.kt  - Creation ViewModel
  components/                  - Components (create, import, dialogs)
```

## Key components

- `ScheduleCreatorSheet` - bottom sheet for schedule creation/import
- `ScheduleCreatorViewModel` - creation management ViewModel
- `components/CreateEvent` - schedule creation event
- `components/CreateState` - creation state
- `components/ImportEvent` - schedule import event
- `components/ImportState` - import state
- `components/ReadPermissionDeniedDialog` - permission denial dialog
- `components/ScheduleCreateDialog` - schedule creation dialog

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`

## Technologies

- Jetpack Compose, Material 3
- Hilt (DI)