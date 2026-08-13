# Модуль data/news-core (`:data:news-core`)

Data-слой новостей. Реализует репозитории для загрузки и хранения новостей: RSS-парсинг, API новостей университета, Room-база данных, мапперы, настройки.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/news/core/data/
  api/                         - API-интерфейсы (RSS, университет)
  db/                          - Room-БД (DAO, Entity, Database)
  di/                          - DI-модули (БД, сеть)
  mapper/                      - Маппер новостей
  repository/                  - Реализации репозиториев
```

## Ключевые компоненты

### api
- `StankinRssApi` - Retrofit-интерфейс RSS-ленты
- `StankinUniversityNewsAPI` - Retrofit-интерфейс API новостей университета

### db
- `NewsDao` - Room DAO для новостей
- `NewsDatabase` - Room-база данных новостей
- `NewsEntity` - Room-сущность новости

### di
- `NewsDatabaseModule` - Hilt-модуль Room-БД
- `NewsModule` - Hilt-модуль API и репозиториев

### mapper
- `NewsMapper` - маппер между Entity и доменной моделью

### repository
- `NewsPreferenceRepositoryImpl` - реализация настроек новостей
- `NewsStorageRepositoryImpl` - реализация локального хранения новостей
- `RssParser` - парсер RSS-ленты
- `UniversityNewsRepositoryImpl` - реализация репозитория новостей университета

## Зависимости

- `:domain:core`
- `:domain:news-core`

## Технологии

- Retrofit (сетевые запросы)
- Room (локальная БД)
- Jsoup (HTML-парсинг)
- Hilt (DI)