# Модуль data/schedule-table (`:data:schedule-table`)

Data-слой таблиц расписания. Реализует создание таблиц расписания и доступ к публичному хранилищу устройства для экспорта.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/table/data/
  di/                          - DI-модуль таблиц
  repository/                  - Реализации провайдера и создателя таблиц
```

## Ключевые компоненты

### di
- `ScheduleTableModule` - Hilt-модуль: провайдер и создатель таблиц

### repository
- `AndroidPublicProviderImpl` - реализация доступа к публичному хранилищу
- `AndroidTableCreatorImpl` - реализация создания таблицы расписания

## Зависимости

- `:domain:core`
- `:domain:schedule-table`

## Технологии

- Hilt (DI)