# Модуль ui/schedule-repository (`:ui:schedule-repository`)

UI-модуль репозитория расписаний ЭОС. Содержит экран с фильтрами (курс, категория) и списком доступных расписаний для загрузки из Moodle, с фоновой загрузкой через WorkManager.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/repository/ui/
  ScheduleRepositoryActivity.kt  - Activity репозитория
  ScheduleRepositoryScreen.kt    - Compose-экран репозитория
  ScheduleRepositoryViewModel.kt - ViewModel репозитория
  components/                    - Компоненты (фильтры, списки, диалоги)
  worker/                        - Worker фоновой загрузки
```

## Ключевые компоненты

- `ScheduleRepositoryActivity` - Activity репозитория расписаний
- `ScheduleRepositoryScreen` - Compose-экран репозитория
- `ScheduleRepositoryViewModel` - ViewModel репозитория
- `components/BackLayerContent` - контент заднего слоя (фильтры)
- `components/FrontLayerContent` - контент переднего слоя (список)
- `components/FilterRow` - строка фильтра
- `components/RepositorySchedule` - элемент расписания в списке
- `components/RepositoryToolBar` - тулбар репозитория
- `components/RepositoryError` - отображение ошибки
- `components/RepositoryLoading` - отображение загрузки
- `components/ChooseNameDialog` - диалог выбора имени
- `components/RequiredNameDialog` - диалог обязательного имени
- `components/DownloadEvent` - событие загрузки
- `components/DownloadState` - состояние загрузки
- `worker/ScheduleDownloadWorker` - Worker фоновой загрузки расписания

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-repository`

## Технологии

- Jetpack Compose, Material 3
- WorkManager (фоновая загрузка)
- Hilt (DI)