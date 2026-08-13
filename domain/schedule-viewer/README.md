# Модуль domain/schedule-viewer (`:domain:schedule-viewer`)

Domain-слой просмотра расписания. Содержит доменные модели для отображения (день, пара, контент), интерфейс репозитория просмотра и use case.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/viewer/domain/
  model/                       - Модели для отображения расписания
  repository/                  - Интерфейс репозитория просмотра
  usecase/                     - Use case просмотра
```

## Ключевые компоненты

### model
- `ScheduleViewDay` - день расписания для отображения
- `ScheduleViewPair` - пара для отображения
- `ViewContent` - базовый контент ячейки
- `TextContent` - текстовый контент
- `LinkContent` - контент-ссылка
- `ScheduleViewConstants` - константы отображения

### repository
- `ScheduleViewerRepository` - интерфейс репозитория просмотра расписания

### usecase
- `ScheduleViewerUseCase` - use case просмотра расписания

## Зависимости

- `:domain:core`
- `:domain:schedule-core`

## Технологии

- Kotlin Coroutines
- Hilt (DI)