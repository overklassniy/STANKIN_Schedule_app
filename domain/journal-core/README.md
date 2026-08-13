# Модуль domain/journal-core (`:domain:journal-core`)

Domain-слой модульного журнала. Содержит доменные модели (студент, оценки, дисциплины), интерфейсы репозиториев (авторизация, хранение, пагинация), use case (логин, обновление, прогноз оценок).

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/journal/core/domain/
  exceptions/                  - Исключения журнала
  model/                       - Доменные модели
  repository/                  - Интерфейсы репозиториев
  usecase/                     - Use case журнала
  usecase/predict/             - Логика прогноза оценок
  utils/                       - Константы
```

## Ключевые компоненты

### exceptions
- `StudentAuthorizedException` - исключение авторизации студента

### model
- `Student` - модель студента
- `StudentCredentials` - учётные данные студента
- `Discipline` - модель дисциплины
- `MarkType` - тип оценки
- `SemesterMarks` - оценки за семестр

### repository
- `JournalRepository` - интерфейс репозитория журнала
- `JournalPagingRepository` - интерфейс пагинации оценок
- `JournalPreference` - интерфейс настроек журнала
- `JournalSecureRepository` - интерфейс безопасного хранения
- `JournalServiceRepository` - интерфейс сервисного репозитория
- `JournalStorageRepository` - интерфейс локального хранилища

### usecase
- `LoginUseCase` - use case авторизации
- `JournalUseCase` - use case работы с журналом
- `JournalUpdateUseCase` - use case обновления оценок
- `PredictUseCase` - use case прогноза оценок
- `predict/PredictCalculater` - калькулятор прогноза оценок

### utils
- `Constants` - константы журнала

## Зависимости

- `:domain:core`

## Технологии

- Kotlin Coroutines
- Paging (пагинация)
- Hilt (DI)