# Модуль data/news-widget (`:data:news-widget`)

Data-слой виджета новостей. Предоставляет реализацию репозитория настроек виджета новостей для Glance-виджета.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/news/widget/data/
  di/                          - DI-модуль виджета новостей
  repository/                  - Реализация репозитория настроек
```

## Ключевые компоненты

### di
- `NewsWidgetModule` - Hilt-модуль: репозиторий настроек виджета

### repository
- `NewsWidgetPreferenceImpl` - реализация репозитория настроек виджета новостей

## Зависимости

- `:domain:core`
- `:domain:news-widget`

## Технологии

- DataStore (настройки)
- Hilt (DI)