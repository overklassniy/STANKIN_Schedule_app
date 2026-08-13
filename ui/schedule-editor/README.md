# Модуль ui/schedule-editor (`:ui:schedule-editor`)

UI-модуль редактирования пары. Содержит экран и ViewModel для создания и редактирования пары (занятия), включая редактирование дат, времени, аудитории, преподавателя.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/editor/ui/
  PairEditorActivity.kt        - Activity редактирования пары
  PairEditorScreen.kt          - Compose-экран редактирования
  PairEditorViewModel.kt       - ViewModel редактирования
  DateEditorBottomSheet.kt     - Bottom sheet редактирования дат
  components/                  - Компоненты (даты, режим, тулбар)
```

## Ключевые компоненты

- `PairEditorActivity` - Activity редактирования пары
- `PairEditorScreen` - Compose-экран редактирования пары
- `PairEditorViewModel` - ViewModel редактирования
- `DateEditorBottomSheet` - bottom sheet для редактирования дат
- `components/EditorMode` - режим редактора (создание/редактирование)
- `components/EditorToolbar` - тулбар редактора
- `components/PairEditorState` - состояние редактора
- `components/DateChip` - чип даты
- `components/OutlinedDateField` - поле ввода даты с обводкой
- `components/DateError` - отображение ошибки даты
- `components/DateEditRequest` - запрос редактирования даты
- `components/DateRequest` - запрос даты
- `components/DateResult` - результат ввода даты

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`

## Технологии

- Jetpack Compose, Material 3
- Hilt (DI)