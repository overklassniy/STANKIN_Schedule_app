# Модуль ui/schedule-viewer (`:ui:schedule-viewer`)

UI-модуль просмотра расписания. Содержит основной экран просмотра расписания с календарём, пагинацией по дням, экспортом в iCal и управлением расписаниями (переименование, удаление, создание).

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/viewer/ui/
  ScheduleViewerScreen.kt      - Compose-экран просмотра
  ScheduleViewerViewModel.kt   - ViewModel просмотра
  components/                  - Компоненты (тулбар, диалоги, экспорт)
```

## Ключевые компоненты

- `ScheduleViewerScreen` - Compose-экран просмотра расписания
- `ScheduleViewerViewModel` - ViewModel просмотра расписания
- `components/ScheduleViewerToolBar` - тулбар просмотра
- `components/ScheduleCalendarDialog` - диалог выбора даты в календаре
- `components/ScheduleCreateDialog` - диалог создания расписания
- `components/ScheduleRemoveDialog` - диалог удаления расписания
- `components/ScheduleState` - состояние экрана просмотра
- `components/RenameEvent` - событие переименования
- `components/RenameState` - состояние переименования
- `components/SaveFormatDialog` - диалог выбора формата сохранения
- `components/ExportFormat` - формат экспорта
- `components/ExportProgress` - индикатор прогресса экспорта

## Зависимости

- `:ui:core`
- `:ui:schedule-core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-ical`
- `:domain:schedule-viewer`
- `:domain:schedule-settings`

## Технологии

- Jetpack Compose, Material 3
- Paging (пагинация по дням)
- Accompanist (разрешения)
- Hilt (DI)