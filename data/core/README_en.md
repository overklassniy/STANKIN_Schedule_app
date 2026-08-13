# Module data/core (`:data:core`)

Base data layer of the application. Provides shared infrastructure for all data modules: GitHub API for update checks, caching system, DataStore for preferences, device and update repositories.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/core/data/
  api/                         - API interfaces
  cache/                       - Caching system
  di/                          - DI modules (Retrofit, DataStore)
  mapper/                      - Type mappers
  preference/                  - DataStore for app preferences
  repository/                  - Repository implementations
```

## Key components

### api
- `GitHubApi` - Retrofit interface for GitHub API (update checks)

### cache
- `CacheManager` - data cache management
- `CacheObject` - cached object wrapper

### di
- `CoreModule` - Hilt module: Retrofit, OkHttpClient, DataStore
- `DeviceCoreModule` - Hilt module: device and update repositories

### mapper
- `DateTimeTypeConverter` - date/time converter for DataStore

### preference
- `AppPreferenceDataStore` - DataStore for app preferences (theme, language)

### repository
- `DeviceRepositoryImpl` - device info repository implementation
- `GooglePlayAvailabilityRepositoryImpl` - Google Play availability check
- `UpdateRepositoryImpl` - app update repository implementation

## Dependencies

- `:domain:core`

## Technologies

- Retrofit, OkHttp (network requests)
- DataStore (preferences)
- Paging (pagination)
- Hilt (DI)