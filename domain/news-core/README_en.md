# Module domain/news-core (`:domain:news-core`)

Domain layer for news. Contains news domain models (post, subdivision), repository interfaces (remote, local, preferences), news review use case.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/news/core/domain/
  model/                       - Domain models
  repository/                  - Repository interfaces
  usecase/                     - News use cases
```

## Key components

### model
- `NewsPost` - news post model
- `NewsSubdivision` - subdivision model (news source)

### repository
- `NewsRemoteRepository` - remote news repository interface
- `NewsStorageRepository` - local news storage interface
- `NewsPreferenceRepository` - news preferences interface

### usecase
- `NewsReviewUseCase` - news review use case

## Dependencies

- `:domain:core`

## Technologies

- Kotlin Coroutines
- Hilt (DI)