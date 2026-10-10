# Module ui/schedule-viewer (`:ui:schedule-viewer`)

UI module for schedule viewing. Contains main schedule viewing screen with calendar, day pagination, iCal export and schedule management (rename, delete, create).

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/viewer/ui/
  ScheduleViewerScreen.kt      - Viewer Compose screen
  ScheduleViewerViewModel.kt   - Viewer ViewModel
  components/                  - Components (toolbar, dialogs, export)
```

## Key components

- `ScheduleViewerScreen` - schedule viewer Compose screen
- `ScheduleViewerViewModel` - schedule viewer ViewModel
- `components/ScheduleViewerToolBar` - viewer toolbar
- `components/ScheduleCalendarDialog` - calendar date selection dialog
- `components/ScheduleRenameDialog` - schedule rename dialog
- `components/ScheduleRemoveDialog` - schedule removal dialog
- `components/ScheduleState` - viewer screen state
- `components/RenameEvent` - rename event
- `components/RenameState` - rename state
- `components/SaveFormatDialog` - save format selection dialog
- `components/ExportFormat` - export format
- `components/ExportProgress` - export progress indicator

## Dependencies

- `:ui:core`
- `:ui:schedule-core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-ical`
- `:domain:schedule-viewer`
- `:domain:schedule-settings`

## Technologies

- Jetpack Compose, Material 3
- Paging (day pagination)
- Accompanist (permissions)
- Hilt (DI)