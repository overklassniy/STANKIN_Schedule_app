# Модуль ui/journal-viewer (`:ui:journal-viewer`)

UI-модуль просмотра модульного журнала. Содержит экран с оценками по семестрам, таблицу оценок, информацию о студенте и Worker для фонового обновления оценок.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/journal/viewer/ui/
  JournalScreen.kt             - Compose-экран журнала
  JournalViewModel.kt          - ViewModel журнала
  components/                  - Компоненты (таблица, вкладки, ошибки)
  view/                        - Представление таблицы оценок
  worker/                      - Worker обновления оценок
```

## Ключевые компоненты

- `JournalScreen` - Compose-экран журнала
- `JournalViewModel` - ViewModel журнала
- `components/MarksTable` - таблица оценок
- `components/SemesterTabRow` - вкладки выбора семестра
- `components/StudentInfo` - информация о студенте
- `components/JournalToolBar` - тулбар журнала
- `components/JournalError` - отображение ошибки
- `components/JournalLoading` - отображение загрузки
- `view/MarksTableView` - представление таблицы оценок
- `worker/JournalMarksUpdateWorker` - Worker фонового обновления оценок

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:journal-core`

## Технологии

- Jetpack Compose, Material 3
- WorkManager (фоновое обновление)
- Hilt (DI)