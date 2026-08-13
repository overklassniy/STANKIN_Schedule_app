# Модуль domain/core (`:domain:core`)

Базовый domain-слой приложения. Содержит общие доменные модели, интерфейсы репозиториев, use case, настройки приложения (тема, язык, цвета), систему кеширования и вспомогательные расширения.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/core/domain/
  cache/                       - Контейнеры кеша
  ext/                         - Расширения (Collections, JodaTime)
  model/                       - Доменные модели (AppUpdate)
  repository/                  - Интерфейсы репозиториев и заглушки
  settings/                    - Настройки приложения
  usecase/                     - Use case устройства
```

## Ключевые компоненты

### cache
- `CacheContainer` - контейнер для кеширования данных

### ext
- `CollectionExtentions` - расширения коллекций
- `JodaTimeExtentions` - расширения Joda-Time

### model
- `AppUpdate` - модель обновления приложения

### repository
- `CrashReporter` - интерфейс отчётности об ошибках
- `ConsoleCrashReporter` - консольная реализация (для debug)
- `ProductAnalytics` - интерфейс продуктовой аналитики
- `ConsoleProductAnalytics` - консольная реализация (для debug)
- `DeviceRepository` - интерфейс репозитория устройства
- `GooglePlayAvailabilityRepository` - интерфейс проверки Google Play
- `UpdateRepository` - интерфейс репозитория обновлений

### settings
- `ApplicationPreference` - модель настроек приложения
- `PreferenceRepository` - интерфейс репозитория настроек
- `AppLanguage` - язык приложения
- `DarkMode` - режим тёмной темы
- `ThemePreset` - пресет темы
- `ThemeColors` - цвета темы
- `KeyColors` - ключевые цвета

### usecase
- `DeviceUseCase` - use case для работы с устройством

## Зависимости

Нет зависимостей от других модулей проекта. Базовый модуль.

## Технологии

- Kotlin Coroutines
- Joda-Time (дата/время)
- Gson (JSON)
- Hilt (DI)