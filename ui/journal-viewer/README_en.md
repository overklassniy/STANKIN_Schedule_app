# Module ui/journal-viewer (`:ui:journal-viewer`)

UI module for module journal viewing. Contains screen with semester grades, marks table, student info and Worker for background marks updates.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/journal/viewer/ui/
  JournalScreen.kt             - Journal Compose screen
  JournalViewModel.kt          - Journal ViewModel
  components/                  - Components (table, tabs, errors)
  view/                        - Marks table view
  worker/                      - Marks update Worker
```

## Key components

- `JournalScreen` - journal Compose screen
- `JournalViewModel` - journal ViewModel
- `components/MarksTable` - marks table
- `components/SemesterTabRow` - semester selection tabs
- `components/StudentInfo` - student info
- `components/JournalToolBar` - journal toolbar
- `components/JournalError` - error display
- `components/JournalLoading` - loading display
- `view/MarksTableView` - marks table view
- `worker/JournalMarksUpdateWorker` - background marks update Worker

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:journal-core`

## Technologies

- Jetpack Compose, Material 3
- WorkManager (background updates)
- Hilt (DI)