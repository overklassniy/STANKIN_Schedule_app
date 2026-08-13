# Модуль data/journal-core (`:data:journal-core`)

Data-слой модульного журнала. Реализует репозитории для работы с журналом: авторизация, загрузка оценок, пагинация, безопасное хранение учётных данных, локальное кеширование.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/journal/core/data/
  api/                         - API-интерфейс модульного журнала
  di/                          - DI-модуль журнала
  mapper/                      - Мапперы (MarkType, ModuleJournal)
  model/                       - Сетевые модели (MarkResponse, SemestersResponse)
  repository/                  - Реализации репозиториев журнала
  source/                      - Источники данных (SemesterMarksSource)
```

## Ключевые компоненты

### api
- `ModuleJournalAPI` - Retrofit-интерфейс API модульного журнала

### di
- `JournalCoreModule` - Hilt-модуль: API, репозитории, мапперы

### mapper
- `MarkTypeTypeConverter` - конвертер типа оценки
- `ModuleJournalMapper` - маппер между сетевыми и доменными моделями

### model
- `MarkResponse` - модель ответа с оценками
- `SemestersResponse` - модель ответа со списком семестров

### repository
- `JournalRepositoryImpl` - основная реализация репозитория журнала
- `JournalPagingRepositoryImpl` - реализация пагинации оценок
- `JournalPreferenceImpl` - реализация настроек журнала
- `JournalSecureRepositoryImpl` - безопасное хранение учётных данных
- `JournalServiceRepositoryImpl` - реализация сервисного репозитория
- `JournalStorageRepositoryImpl` - локальное хранение данных журнала

### source
- `SemesterMarksSource` - источник данных оценок для Paging

## Зависимости

- `:domain:core`
- `:domain:journal-core`

## Технологии

- Retrofit (сетевые запросы)
- Paging (пагинация)
- Security Crypto (безопасное хранение)
- Hilt (DI)