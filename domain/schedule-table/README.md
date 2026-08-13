# Модуль domain/schedule-table (`:domain:schedule-table`)

Domain-слой таблиц расписания. Содержит доменные модели таблицы (ячейка, день, конфигурация), интерфейсы для создания и экспорта таблиц, use case.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/table/domain/
  model/                       - Модели таблицы расписания
  repository/                  - Интерфейсы провайдера и создателя
  usecase/                     - Use case таблицы
```

## Ключевые компоненты

### model
- `ScheduleTable` - модель таблицы расписания
- `ScheduleTableCell` - ячейка таблицы
- `ScheduleTableDay` - день в таблице
- `TableConfig` - конфигурация таблицы
- `TableMode` - режим отображения таблицы
- `DrawScheduleTable` - модель для отрисовки таблицы
- `DrawScheduleTableCell` - ячейка для отрисовки
- `DrawExtentions` - расширения для отрисовки

### repository
- `AndroidPublicProvider` - интерфейс доступа к публичному хранилищу
- `AndroidTableCreator` - интерфейс создания таблицы

### usecase
- `AndroidTableUseCase` - use case таблицы расписания
- `AndroidTablePainter` - отрисовщик таблицы

## Зависимости

- `:domain:core`
- `:domain:schedule-core`

## Технологии

- Kotlin Coroutines
- Hilt (DI)