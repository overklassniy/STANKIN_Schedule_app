# Модуль ui/schedule-list (`:ui:schedule-list`)

UI-модуль списка расписаний. Содержит экран со списком сохранённых расписаний, действиями (удаление, переименование, выбор) и диалогом подтверждения удаления.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/list/ui/
  ScheduleScreen.kt            - Compose-экран списка расписаний
  ScheduleScreenViewModel.kt   - ViewModel списка
  components/                  - Компоненты (элемент, тулбар, диалоги)
```

## Ключевые компоненты

- `ScheduleScreen` - Compose-экран списка расписаний
- `ScheduleScreenViewModel` - ViewModel списка расписаний
- `components/ScheduleItem` - элемент списка расписаний
- `components/ScheduleToolBar` - тулбар списка
- `components/ScheduleActionItem` - элемент действия
- `components/ScheduleActionToolBar` - тулбар действий
- `components/ScheduleRemoveDialog` - диалог удаления расписания

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`

## Технологии

- Jetpack Compose, Material 3
- Hilt (DI)