# Module domain/core (`:domain:core`)

Base domain layer of the application. Contains shared domain models, repository interfaces, use cases, app settings (theme, language, colors), caching system and utility extensions.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/core/domain/
  cache/                       - Cache containers
  ext/                         - Extensions (Collections, JodaTime)
  model/                       - Domain models (AppUpdate)
  repository/                  - Repository interfaces and stubs
  settings/                    - App settings
  usecase/                     - Device use case
```

## Key components

### cache
- `CacheContainer` - data caching container

### ext
- `CollectionExtentions` - collection extensions
- `JodaTimeExtentions` - Joda-Time extensions

### model
- `AppUpdate` - app update model

### repository
- `CrashReporter` - crash reporting interface
- `ConsoleCrashReporter` - console implementation (for debug)
- `ProductAnalytics` - product analytics interface
- `ConsoleProductAnalytics` - console implementation (for debug)
- `DeviceRepository` - device repository interface
- `GooglePlayAvailabilityRepository` - Google Play availability interface
- `UpdateRepository` - update repository interface

### settings
- `ApplicationPreference` - app preferences model
- `PreferenceRepository` - preferences repository interface
- `AppLanguage` - app language
- `DarkMode` - dark mode setting
- `ThemePreset` - theme preset
- `ThemeColors` - theme colors
- `KeyColors` - key colors

### usecase
- `DeviceUseCase` - device use case

## Dependencies

No dependencies on other project modules. Base module.

## Technologies

- Kotlin Coroutines
- Joda-Time (date/time)
- Gson (JSON)
- Hilt (DI)