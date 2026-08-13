# Module data/news-widget (`:data:news-widget`)

Data layer for the news widget. Provides implementation of the news widget preferences repository for the Glance widget.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/news/widget/data/
  di/                          - News widget DI module
  repository/                  - Preferences repository implementation
```

## Key components

### di
- `NewsWidgetModule` - Hilt module: widget preferences repository

### repository
- `NewsWidgetPreferenceImpl` - news widget preferences repository implementation

## Dependencies

- `:domain:core`
- `:domain:news-widget`

## Technologies

- DataStore (preferences)
- Hilt (DI)