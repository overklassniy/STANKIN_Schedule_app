# Module data/journal-core (`:data:journal-core`)

Data layer for the module journal. Implements repositories for journal operations: OIDC authentication (PKCE) against the lko.stankin.ru personal account, loading grades, pagination, secure token storage, local caching.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/journal/core/data/
  api/                         - Module journal API interface
  di/                          - Journal DI module
  mapper/                      - Mappers (MarkType, ModuleJournal)
  model/                       - Network models (study progress, group, token responses)
  repository/                  - Journal repository implementations
  source/                      - Data sources (SemesterMarksSource)
  utils/                       - Utilities (PkceGenerator)
```

## Key components

### api
- `ModuleJournalAPI` - Retrofit interface for lko-api.stankin.ru (Bearer authorization)

### di
- `JournalCoreModule` - Hilt module: API, repositories, mappers

### mapper
- `MarkTypeTypeConverter` - mark type converter
- `ModuleJournalMapper` - mapper between network and domain models

### model
- `ExternalStudyProgressResponse` - study progress API response
- `AllowedSemesterResponse` - allowed semester identifier
- `PeriodResponse` - study progress period (semester)
- `DisciplineResponse` - discipline with marks
- `StudyProgressData` - student data from progress response
- `EduGroupResponse` - study group data
- `TokenResponse` - code-to-token exchange response

### repository
- `JournalAuthRepositoryImpl` - OIDC authentication: auth URL building, code exchange
- `JournalRepositoryImpl` - main journal repository implementation
- `JournalPagingRepositoryImpl` - grade pagination implementation
- `JournalPreferenceImpl` - journal preferences implementation
- `JournalSecureRepositoryImpl` - secure access token storage
- `JournalServiceRepositoryImpl` - service repository implementation
- `JournalStorageRepositoryImpl` - local journal data storage

### source
- `SemesterMarksSource` - Paging data source for marks

### utils
- `PkceGenerator` - PKCE parameter generator (code_verifier, code_challenge, state)

## Dependencies

- `:domain:core`
- `:domain:journal-core`

## Technologies

- Retrofit (network requests)
- Paging (pagination)
- Security Crypto (secure storage)
- Hilt (DI)