# Module ui/schedule-repository (`:ui:schedule-repository`)

UI module for the EOS schedule repository. Contains screen with filters (course, category) and list of available schedules for downloading from Moodle, with background download via WorkManager.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/repository/ui/
  ScheduleRepositoryActivity.kt  - Repository Activity
  ScheduleRepositoryScreen.kt    - Repository Compose screen
  ScheduleRepositoryViewModel.kt - Repository ViewModel
  components/                    - Components (filters, lists, dialogs)
  worker/                        - Background download Worker
```

## Key components

- `ScheduleRepositoryActivity` - schedule repository Activity
- `ScheduleRepositoryScreen` - repository Compose screen
- `ScheduleRepositoryViewModel` - repository ViewModel
- `components/BackLayerContent` - back layer content (filters)
- `components/FrontLayerContent` - front layer content (list)
- `components/FilterRow` - filter row
- `components/RepositorySchedule` - schedule list item
- `components/RepositoryToolBar` - repository toolbar
- `components/RepositoryError` - error display
- `components/RepositoryLoading` - loading display
- `components/ChooseNameDialog` - name selection dialog
- `components/RequiredNameDialog` - required name dialog
- `components/DownloadEvent` - download event
- `components/DownloadState` - download state
- `worker/ScheduleDownloadWorker` - background schedule download Worker

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-repository`

## Technologies

- Jetpack Compose, Material 3
- WorkManager (background download)
- Hilt (DI)