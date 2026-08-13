# Module domain/news-widget (`:domain:news-widget`)

Domain layer for the news widget. Contains widget data domain model, preferences repository interface, and use cases for widget configuration and display.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/news/widget/domain/
  model/                       - Widget domain model
  repository/                  - Preferences repository interface
  usecase/                     - Widget use cases
```

## Key components

### model
- `NewsWidgetData` - news widget data model

### repository
- `NewsWidgetPreference` - widget preferences repository interface

### usecase
- `NewsConfigureUseCase` - widget configuration use case
- `NewsWidgetUseCase` - widget update use case

## Dependencies

- `:domain:core`

## Technologies

- Kotlin Coroutines
- Hilt (DI)