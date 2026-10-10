# Module domain/journal-core (`:domain:journal-core`)

Domain layer for the module journal. Contains domain models (student, marks, disciplines, semester, OIDC token), repository interfaces (OIDC authentication, storage, pagination), use cases (login, update, grade prediction).

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/journal/core/domain/
  exceptions/                  - Journal exceptions
  model/                       - Domain models
  repository/                  - Repository interfaces
  usecase/                     - Journal use cases
  usecase/predict/             - Grade prediction logic
  utils/                       - Constants
```

## Key components

### exceptions
- `StudentAuthorizedException` - student authorization exception

### model
- `Student` - student model
- `JournalSemester` - semester identifier (course, semester)
- `JournalAuthToken` - personal account API access token
- `JournalAuthRequest` - prepared OIDC authorization request
- `Discipline` - discipline model
- `MarkType` - mark type
- `SemesterMarks` - semester marks

### repository
- `JournalAuthRepository` - OIDC authorization interface (PKCE)
- `JournalRepository` - journal repository interface
- `JournalPagingRepository` - mark pagination interface
- `JournalPreference` - journal preferences interface
- `JournalSecureRepository` - secure token storage interface
- `JournalServiceRepository` - service repository interface
- `JournalStorageRepository` - local storage interface

### usecase
- `LoginUseCase` - authentication use case
- `JournalUseCase` - journal use case
- `JournalUpdateUseCase` - marks update use case
- `PredictUseCase` - grade prediction use case
- `predict/PredictCalculater` - grade prediction calculator

### utils
- `Constants` - journal constants

## Dependencies

- `:domain:core`

## Technologies

- Kotlin Coroutines
- Paging (pagination)
- Hilt (DI)