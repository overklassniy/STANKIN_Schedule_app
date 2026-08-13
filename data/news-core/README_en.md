# Module data/news-core (`:data:news-core`)

Data layer for news. Implements repositories for loading and storing news: RSS parsing, university news API, Room database, mappers, preferences.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/news/core/data/
  api/                         - API interfaces (RSS, university)
  db/                          - Room DB (DAO, Entity, Database)
  di/                          - DI modules (DB, network)
  mapper/                      - News mapper
  repository/                  - Repository implementations
```

## Key components

### api
- `StankinRssApi` - Retrofit interface for RSS feed
- `StankinUniversityNewsAPI` - Retrofit interface for university news API

### db
- `NewsDao` - Room DAO for news
- `NewsDatabase` - Room database for news
- `NewsEntity` - Room news entity

### di
- `NewsDatabaseModule` - Hilt module for Room DB
- `NewsModule` - Hilt module for API and repositories

### mapper
- `NewsMapper` - mapper between Entity and domain model

### repository
- `NewsPreferenceRepositoryImpl` - news preferences implementation
- `NewsStorageRepositoryImpl` - local news storage implementation
- `RssParser` - RSS feed parser
- `UniversityNewsRepositoryImpl` - university news repository implementation

## Dependencies

- `:domain:core`
- `:domain:news-core`

## Technologies

- Retrofit (network requests)
- Room (local DB)
- Jsoup (HTML parsing)
- Hilt (DI)