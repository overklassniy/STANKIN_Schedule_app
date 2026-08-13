# Module domain/schedule-repository (`:domain:schedule-repository`)

Domain layer for the EOS schedule repository. Contains domain models (course, grade, category, repository item), repository interfaces (storage, loader, remote service) and use cases.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/repository/domain/
  model/                       - Repository domain models
  repository/                  - Repository interfaces
  usecase/                     - Repository use cases
```

## Key components

### model
- `RepositoryItem` - schedule repository item
- `RepositoryDescription` - repository item description
- `RepositoryCategory` - repository category
- `Course` - course
- `Grade` - grade (year of study)

### repository
- `RepositoryStorage` - local repository storage interface
- `ScheduleLoaderService` - schedule loading service interface
- `ScheduleRemoteService` - remote service interface

### usecase
- `RepositoryUseCase` - repository use case
- `RepositoryLoaderUseCase` - schedule loading use case

## Dependencies

- `:domain:core`
- `:domain:schedule-core`

## Technologies

- Kotlin Coroutines
- Hilt (DI)