# Module data/schedule-repository (`:data:schedule-repository`)

Data layer for the EOS schedule repository. Implements loading schedules from Moodle repository (Electronic Educational Environment of MSTU STANKIN), local caching via Room, background loading via WorkManager.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/repository/data/
  db/                          - Room DB (DAO, Entity, Database)
  di/                          - DI modules (DB, network, repositories)
  mapper/                      - Repository mapper
  repository/                  - Service and storage implementations
```

## Key components

### db
- `RepositoryDao` - Room DAO for schedule repository
- `RepositoryDatabase` - Room database for repository
- `RepositoryEntity` - Room repository item entity

### di
- `RepositoryDatabaseModule` - Hilt module for Room DB
- `RepositoryModule` - Hilt module: mappers, storage
- `RepositoryNetworkModule` - Hilt module: network services

### mapper
- `RepositoryMapper` - mapper between Entity and domain model

### repository
- `RepositoryStorageImpl` - local repository storage implementation
- `MoodleLoaderService` - schedule loading service from Moodle
- `MoodleRemoteService` - remote Moodle request service

## Dependencies

- `:data:core`
- `:data:schedule-core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-repository`
- `:domain:schedule-parser`

## Technologies

- Room (local DB)
- Retrofit, Jsoup (network requests, HTML parsing)
- WorkManager (background loading)
- Hilt (DI)