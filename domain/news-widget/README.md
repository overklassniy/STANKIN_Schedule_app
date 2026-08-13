# Модуль domain/news-widget (`:domain:news-widget`)

Domain-слой виджета новостей. Содержит доменную модель данных виджета, интерфейс репозитория настроек и use case для конфигурации и отображения виджета.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/news/widget/domain/
  model/                       - Доменная модель виджета
  repository/                  - Интерфейс репозитория настроек
  usecase/                     - Use case виджета
```

## Ключевые компоненты

### model
- `NewsWidgetData` - модель данных виджета новостей

### repository
- `NewsWidgetPreference` - интерфейс репозитория настроек виджета

### usecase
- `NewsConfigureUseCase` - use case конфигурации виджета
- `NewsWidgetUseCase` - use case обновления виджета

## Зависимости

- `:domain:core`

## Технологии

- Kotlin Coroutines
- Hilt (DI)