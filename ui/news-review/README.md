# Модуль ui/news-review (`:ui:news-review`)

UI-модуль обзора новостей. Содержит компоненты ленты новостей: посты, вкладки, индикаторы загрузки и ошибок. Используется как вкладка на главном экране.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/news/review/ui/components/
  NewsPost.kt                  - Compose-компонент поста новости
  NewsPostColumn.kt            - Колонка постов новостей
  NewsReviewToolBar.kt         - Тулбар обзора новостей
  NewsError.kt                 - Отображение ошибки загрузки
  NewsLoading.kt               - Отображение загрузки
  AppTabIndicator.kt           - Индикатор вкладок
```

## Ключевые компоненты

- `NewsPost` - Compose-компонент карточки поста новости
- `NewsPostColumn` - колонка постов с пагинацией
- `NewsReviewToolBar` - тулбар с вкладками источников новостей
- `NewsError` - отображение ошибки загрузки новостей
- `NewsLoading` - отображение загрузки новостей
- `AppTabIndicator` - индикатор активной вкладки

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:news-core`

## Технологии

- Jetpack Compose, Material 3