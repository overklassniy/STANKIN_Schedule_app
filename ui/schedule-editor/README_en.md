# Module ui/schedule-editor (`:ui:schedule-editor`)

UI module for pair editing. Contains screen and ViewModel for creating and editing a pair (class), including editing dates, time, classroom, teacher.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/editor/ui/
  PairEditorActivity.kt        - Pair editing Activity
  PairEditorScreen.kt          - Compose editing screen
  PairEditorViewModel.kt       - Editing ViewModel
  DateEditorBottomSheet.kt     - Date editing bottom sheet
  components/                  - Components (dates, mode, toolbar)
```

## Key components

- `PairEditorActivity` - pair editing Activity
- `PairEditorScreen` - Compose pair editing screen
- `PairEditorViewModel` - editing ViewModel
- `DateEditorBottomSheet` - bottom sheet for date editing
- `components/EditorMode` - editor mode (create/edit)
- `components/EditorToolbar` - editor toolbar
- `components/PairEditorState` - editor state
- `components/DateChip` - date chip
- `components/OutlinedDateField` - outlined date input field
- `components/DateError` - date error display
- `components/DateEditRequest` - date edit request
- `components/DateRequest` - date request
- `components/DateResult` - date input result

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`

## Technologies

- Jetpack Compose, Material 3
- Hilt (DI)