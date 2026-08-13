# Модуль data/schedule-settings (`:data:schedule-settings`)

Data-слой настроек отображения расписания. Реализует хранение настроек (цвета по типам пар, группы) через DataStore.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/settings/data/
  di/                          - DI-модуль настроек
  repository/                  - Реализация DataStore-хранилища
```

## Ключевые компоненты

### di
- `ScheduleSettingsModule` - Hilt-модуль: DataStore настроек расписания

### repository
- `ScheduleDataStore` - реализация хранилища настроек отображения расписания через DataStore

## Зависимости

- `:domain:core`
- `:domain:schedule-settings`

## Технологии

- DataStore (настройки)
- Hilt (DI)