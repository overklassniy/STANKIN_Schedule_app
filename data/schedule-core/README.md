# Модуль data/schedule-core (`:data:schedule-core`)

Core data-слой расписания. Реализует хранение расписаний в Room-БД, JSON-сериализацию/десериализацию, мапперы между Entity и доменными моделями, репозиторий хранилища расписаний.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/core/data/
  api/                         - JSON-модель пары
  db/                          - Room-БД (DAO, Entity, Database, ScheduleWithPairs)
  di/                          - DI-модули (Room, репозитории)
  mapper/                      - Мапперы (DateTime, Database, Json, JsonUtils)
  repository/                  - Реализации репозиториев
```

## Ключевые компоненты

### api
- `PairJson` - JSON-модель пары для сериализации

### db
- `ScheduleDao` - Room DAO для расписаний и пар
- `ScheduleDatabase` - Room-база данных расписаний
- `ScheduleEntity` - Room-сущность расписания
- `PairEntity` - Room-сущность пары
- `ScheduleWithPairs` - связь расписания с парами

### di
- `ScheduleCoreModule` - Hilt-модуль: мапперы, репозитории
- `ScheduleDatabaseModule` - Hilt-модуль: Room-БД, DAO

### mapper
- `DateTimeConverter` - конвертер даты/времени
- `ScheduleDatabaseMapper` - маппер между Entity и доменной моделью
- `ScheduleJsonMapper` - маппер между JSON и доменной моделью
- `ScheduleJsonUtils` - утилиты JSON-сериализации

### repository
- `ScheduleStorageImpl` - реализация хранилища расписаний
- `ScheduleDeviceRepositoryImpl` - реализация репозитория устройства

## Зависимости

- `:data:core`
- `:domain:core`
- `:domain:schedule-core`
- `:data:schedule-viewer`

## Технологии

- Room (локальная БД)
- Retrofit, Gson (JSON)
- Paging (пагинация)
- Hilt (DI)