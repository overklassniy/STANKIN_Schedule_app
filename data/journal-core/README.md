# Модуль data/journal-core (`:data:journal-core`)

Data-слой модульного журнала. Реализует репозитории для работы с журналом: OIDC-авторизация (PKCE) в личном кабинете lko.stankin.ru, загрузка оценок, пагинация, безопасное хранение токена, локальное кеширование.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/journal/core/data/
  api/                         - API-интерфейс модульного журнала
  di/                          - DI-модуль журнала
  mapper/                      - Мапперы (MarkType, ModuleJournal)
  model/                       - Сетевые модели (ответы API успеваемости, группы, токена)
  repository/                  - Реализации репозиториев журнала
  source/                      - Источники данных (SemesterMarksSource)
  utils/                       - Утилиты (PkceGenerator)
```

## Ключевые компоненты

### api
- `ModuleJournalAPI` - Retrofit-интерфейс API lko-api.stankin.ru (Bearer-авторизация)

### di
- `JournalCoreModule` - Hilt-модуль: API, репозитории, мапперы

### mapper
- `MarkTypeTypeConverter` - конвертер типа оценки
- `ModuleJournalMapper` - маппер между сетевыми и доменными моделями

### model
- `ExternalStudyProgressResponse` - ответ API успеваемости
- `AllowedSemesterResponse` - идентификатор разрешённого семестра
- `PeriodResponse` - период успеваемости (семестр)
- `DisciplineResponse` - дисциплина с оценками
- `StudyProgressData` - данные студента из ответа успеваемости
- `EduGroupResponse` - данные учебной группы
- `TokenResponse` - ответ обмена кода на токен

### repository
- `JournalAuthRepositoryImpl` - OIDC-авторизация: URL авторизации, обмен кода на токен
- `JournalRepositoryImpl` - основная реализация репозитория журнала
- `JournalPagingRepositoryImpl` - реализация пагинации оценок
- `JournalPreferenceImpl` - реализация настроек журнала
- `JournalSecureRepositoryImpl` - безопасное хранение токена доступа
- `JournalServiceRepositoryImpl` - реализация сервисного репозитория
- `JournalStorageRepositoryImpl` - локальное хранение данных журнала; несовместимый кэш от старых версий считается отсутствующим

### source
- `SemesterMarksSource` - источник данных оценок для Paging

### utils
- `PkceGenerator` - генератор PKCE-параметров (code_verifier, code_challenge, state)

## Зависимости

- `:domain:core`
- `:domain:journal-core`

## Технологии

- Retrofit (сетевые запросы)
- Paging (пагинация)
- Security Crypto (безопасное хранение)
- Hilt (DI)