# Модуль domain/schedule-repository (`:domain:schedule-repository`)

Domain-слой репозитория расписаний ЭОС. Содержит доменные модели (курс, курс обучения, категория, элемент репозитория), интерфейсы репозиториев (хранилище, загрузчик, удалённый сервис) и use case.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/repository/domain/
  model/                       - Доменные модели репозитория
  repository/                  - Интерфейсы репозиториев
  usecase/                     - Use case репозитория
```

## Ключевые компоненты

### model
- `RepositoryItem` - элемент репозитория расписаний
- `RepositoryDescription` - описание элемента репозитория
- `RepositoryCategory` - категория репозитория
- `Course` - курс обучения
- `Grade` - курс/класс (год обучения)

### repository
- `RepositoryStorage` - интерфейс локального хранилища репозитория
- `ScheduleLoaderService` - интерфейс сервиса загрузки расписаний
- `ScheduleRemoteService` - интерфейс удалённого сервиса

### usecase
- `RepositoryUseCase` - use case работы с репозиторием
- `RepositoryLoaderUseCase` - use case загрузки расписаний

## Зависимости

- `:domain:core`
- `:domain:schedule-core`

## Технологии

- Kotlin Coroutines
- Hilt (DI)