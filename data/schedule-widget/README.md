# Модуль data/schedule-widget (`:data:schedule-widget`)

Data-слой виджета расписания. Предоставляет реализацию репозитория настроек виджета расписания для Glance-виджета.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/widget/data/
  di/                          - DI-модуль виджета расписания
  repository/                  - Реализация репозитория настроек
```

## Ключевые компоненты

### di
- `ScheduleWidgetModule` - Hilt-модуль: репозиторий настроек виджета

### repository
- `ScheduleWidgetPreferenceImpl` - реализация репозитория настроек виджета расписания

## Зависимости

- `:domain:core`
- `:domain:schedule-widget`

## Технологии

- DataStore (настройки)
- Hilt (DI)