# Module data/journal-core (`:data:journal-core`)

Data layer for the module journal. Implements repositories for journal operations: authentication, loading grades, pagination, secure credential storage, local caching.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/journal/core/data/
  api/                         - Module journal API interface
  di/                          - Journal DI module
  mapper/                      - Mappers (MarkType, ModuleJournal)
  model/                       - Network models (MarkResponse, SemestersResponse)
  repository/                  - Journal repository implementations
  source/                      - Data sources (SemesterMarksSource)
```

## Key components

### api
- `ModuleJournalAPI` - Retrofit interface for module journal API

### di
- `JournalCoreModule` - Hilt module: API, repositories, mappers

### mapper
- `MarkTypeTypeConverter` - mark type converter
- `ModuleJournalMapper` - mapper between network and domain models

### model
- `MarkResponse` - mark response model
- `SemestersResponse` - semesters list response model

### repository
- `JournalRepositoryImpl` - main journal repository implementation
- `JournalPagingRepositoryImpl` - grade pagination implementation
- `JournalPreferenceImpl` - journal preferences implementation
- `JournalSecureRepositoryImpl` - secure credential storage
- `JournalServiceRepositoryImpl` - service repository implementation
- `JournalStorageRepositoryImpl` - local journal data storage

### source
- `SemesterMarksSource` - Paging data source for marks

## Dependencies

- `:domain:core`
- `:domain:journal-core`

## Technologies

- Retrofit (network requests)
- Paging (pagination)
- Security Crypto (secure storage)
- Hilt (DI)