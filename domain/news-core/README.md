# Модуль domain/news-core (`:domain:news-core`)

Domain-слой новостей. Содержит доменные модели новостей (пост, подразделение), интерфейсы репозиториев (удалённый, локальный, настройки), use case для обзора новостей.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/news/core/domain/
  model/                       - Доменные модели
  repository/                  - Интерфейсы репозиториев
  usecase/                     - Use case новостей
```

## Ключевые компоненты

### model
- `NewsPost` - модель поста новости
- `NewsSubdivision` - модель подразделения (источника новостей)

### repository
- `NewsRemoteRepository` - интерфейс удалённого репозитория новостей
- `NewsStorageRepository` - интерфейс локального хранилища новостей
- `NewsPreferenceRepository` - интерфейс настроек новостей

### usecase
- `NewsReviewUseCase` - use case обзора новостей

## Зависимости

- `:domain:core`

## Технологии

- Kotlin Coroutines
- Hilt (DI)