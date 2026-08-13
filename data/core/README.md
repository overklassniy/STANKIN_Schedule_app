# Модуль data/core (`:data:core`)

Базовый data-слой приложения. Предоставляет общую инфраструктуру для всех data-модулей: GitHub API для проверки обновлений, система кеширования, DataStore для настроек, репозитории устройств и обновлений.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/core/data/
  api/                         - API-интерфейсы
  cache/                       - Система кеширования
  di/                          - DI-модули (Retrofit, DataStore)
  mapper/                      - Мапперы типов
  preference/                  - DataStore для настроек приложения
  repository/                  - Реализации репозиториев
```

## Ключевые компоненты

### api
- `GitHubApi` - Retrofit-интерфейс для GitHub API (проверка обновлений)

### cache
- `CacheManager` - управление кешем данных
- `CacheObject` - обёртка кешируемого объекта

### di
- `CoreModule` - Hilt-модуль: Retrofit, OkHttpClient, DataStore
- `DeviceCoreModule` - Hilt-модуль: репозитории устройств и обновлений

### mapper
- `DateTimeTypeConverter` - конвертер даты/времени для DataStore

### preference
- `AppPreferenceDataStore` - DataStore для настроек приложения (тема, язык)

### repository
- `DeviceRepositoryImpl` - реализация репозитория информации об устройстве
- `GooglePlayAvailabilityRepositoryImpl` - проверка доступности Google Play
- `UpdateRepositoryImpl` - реализация репозитория обновлений приложения

## Зависимости

- `:domain:core`

## Технологии

- Retrofit, OkHttp (сетевые запросы)
- DataStore (настройки)
- Paging (пагинация)
- Hilt (DI)