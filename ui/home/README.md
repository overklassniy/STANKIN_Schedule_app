# Модуль ui/home (`:ui:home`)

UI-модуль главного экрана. Содержит корневой экран с нижней панелью вкладок (расписание, новости), управлением in-app обновлениями и хостингом вкладок.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/home/ui/
  HomeScreen.kt                - Compose-экран с вкладками
  HomeViewModel.kt             - ViewModel главного экрана
  components/                  - Компоненты (вкладки, обновления)
```

## Ключевые компоненты

- `HomeScreen` - Compose-экран с вкладками расписания и новостей
- `HomeViewModel` - ViewModel главного экрана
- `components/HomeTab` - вкладка главного экрана
- `components/HomeTabRow` - панель вкладок
- `components/InAppUpdateEvent` - событие in-app обновления
- `components/InAppUpdateState` - состояние in-app обновления
- `components/InAppUpdateResult` - результат in-app обновления

## Зависимости

- `:ui:core`
- `:domain:core`
- `:ui:news-review`
- `:domain:news-core`
- `:ui:news-widget`
- `:ui:schedule-core`
- `:domain:schedule-viewer`
- `:domain:schedule-settings`
- `:domain:schedule-core`

## Технологии

- Jetpack Compose, Material 3
- In-App Update (обновление приложения)
- Hilt (DI)