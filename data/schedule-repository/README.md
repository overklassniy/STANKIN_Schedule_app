# Модуль data/schedule-repository (`:data:schedule-repository`)

Data-слой репозитория расписаний ЭОС. Реализует загрузку расписаний из репозитория Moodle (Электронная Образовательная Среда МГТУ СТАНКИН), локальное кеширование через Room, фоновую загрузку через WorkManager.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/repository/data/
  db/                          - Room-БД (DAO, Entity, Database)
  di/                          - DI-модули (БД, сеть, репозитории)
  mapper/                      - Маппер репозитория
  repository/                  - Реализации сервисов и хранилища
```

## Ключевые компоненты

### db
- `RepositoryDao` - Room DAO для репозитория расписаний
- `RepositoryDatabase` - Room-база данных репозитория
- `RepositoryEntity` - Room-сущность элемента репозитория

### di
- `RepositoryDatabaseModule` - Hilt-модуль Room-БД
- `RepositoryModule` - Hilt-модуль: мапперы, хранилище
- `RepositoryNetworkModule` - Hilt-модуль: сетевые сервисы

### mapper
- `RepositoryMapper` - маппер между Entity и доменной моделью

### repository
- `RepositoryStorageImpl` - реализация локального хранилища репозитория
- `MoodleLoaderService` - сервис загрузки расписаний из Moodle
- `MoodleRemoteService` - сервис удалённых запросов к Moodle

## Зависимости

- `:data:core`
- `:data:schedule-core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-repository`
- `:domain:schedule-parser`

## Технологии

- Room (локальная БД)
- Retrofit, Jsoup (сетевые запросы, HTML-парсинг)
- WorkManager (фоновая загрузка)
- Hilt (DI)