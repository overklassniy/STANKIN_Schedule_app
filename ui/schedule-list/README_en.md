# Module ui/schedule-list (`:ui:schedule-list`)

UI module for schedule list. Contains screen with list of saved schedules, actions (delete, rename, select) and delete confirmation dialog.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/list/ui/
  ScheduleScreen.kt            - Schedule list Compose screen
  ScheduleScreenViewModel.kt   - List ViewModel
  components/                  - Components (item, toolbar, dialogs)
```

## Key components

- `ScheduleScreen` - schedule list Compose screen
- `ScheduleScreenViewModel` - schedule list ViewModel
- `components/ScheduleItem` - schedule list item
- `components/ScheduleToolBar` - list toolbar
- `components/ScheduleActionItem` - action item
- `components/ScheduleActionToolBar` - action toolbar
- `components/ScheduleRemoveDialog` - schedule removal dialog

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`

## Technologies

- Jetpack Compose, Material 3
- Hilt (DI)