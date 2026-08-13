# Module ui/schedule-table (`:ui:schedule-table`)

UI module for the schedule table. Contains schedule table screen with zoom support, display settings and export to various formats.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/table/ui/
  ScheduleTableScreen.kt       - Table Compose screen
  ScheduleTableViewActivity.kt - Table view Activity
  ScheduleTableViewModel.kt    - Table ViewModel
  components/                  - Components (toolbar, export, settings)
```

## Key components

- `ScheduleTableScreen` - schedule table Compose screen
- `ScheduleTableViewActivity` - table view Activity
- `ScheduleTableViewModel` - table ViewModel
- `components/TableView` - table view
- `components/ZoomableBox` - zoomable container
- `components/ScheduleTableAppBar` - top toolbar
- `components/ScheduleTableBottomAppBar` - bottom toolbar
- `components/SettingsSheet` - table settings bottom sheet
- `components/TableFomatDialog` - table format dialog
- `components/ExportFormat` - export format
- `components/ExportType` - export type
- `components/ExportProgress` - export progress indicator
- `components/ExportSnackbar` - export snackbar

## Dependencies

- `:ui:core`
- `:ui:schedule-core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-table`
- `:domain:schedule-settings`

## Technologies

- Jetpack Compose, Material 3
- Hilt (DI)