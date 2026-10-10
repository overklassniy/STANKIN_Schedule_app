# Модуль app (`:app`)

Главный модуль приложения, точка входа. Содержит `MainActivity`, `MainApplication`, навигацию между экранами, DI-конфигурацию, интеграции Firebase и Sentry.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/
  MainActivity.kt              - Главная Activity, хост для Compose-навигации
  MainApplication.kt           - Класс приложения, инициализация Hilt, Sentry, Firebase
  data/                        - Реализации аналитики и отчётности об ошибках
  di/                          - DI-модуль приложения (AppModule)
  navigation/                  - Граф навигации, нижняя панель, точки входа
  ui/                          - Главный экран и панель навигации
```

## Ключевые компоненты

### data
- `FirebaseProductAnalytics` - реализация продуктовой аналитики через Firebase
- `SentryCrashReporter` - реализация отчётности об ошибках через Sentry

### di
- `AppModule` - Hilt-модуль, предоставляющий реализации аналитики и краш-репортёра

### navigation
- `AppNavigation` - основной граф навигации приложения
- `HomeNavEntry` - точка входа главного экрана
- `ModuleJournalNavigation` - навигация к экранам модульного журнала
- `ScheduleNavigation` - навигация к экранам расписания
- `entry/BottomNavEntry` - элемент нижней панели навигации
- `entry/DestinationNavEntry` - элемент навигации к конкретному экрану
- `entry/NavigationEntry` - базовая модель точки входа навигации

### ui
- `MainScreen` - корневой экран с нижней панелью навигации
- `AppNavigationBar` - компонент нижней панели навигации

## Зависимости

Модуль зависит от всех модулей проекта:

- **data:** `:data:core`, `:data:journal-core`, `:data:news-core`, `:data:news-widget`, `:data:schedule-core`, `:data:schedule-ical`, `:data:schedule-table`, `:data:schedule-parser`, `:data:schedule-repository`, `:data:schedule-settings`, `:data:schedule-viewer`, `:data:schedule-widget`
- **domain:** `:domain:core`, `:domain:news-core`, `:domain:news-widget`, `:domain:schedule-core`, `:domain:schedule-settings`
- **ui:** `:ui:core`, `:ui:home`, `:ui:journal-login`, `:ui:journal-predict`, `:ui:journal-viewer`, `:ui:schedule-creator`, `:ui:schedule-editor`, `:ui:schedule-table`, `:ui:schedule-parser`, `:ui:schedule-list`, `:ui:schedule-repository`, `:ui:schedule-viewer`, `:ui:schedule-widget`, `:ui:news-review`, `:ui:news-widget`, `:ui:settings`

## Технологии

- Jetpack Compose, Material 3
- Navigation Compose
- Hilt (DI)
- Firebase Analytics
- Sentry (краш-репортинг)
- Room (схемы БД)
- WorkManager (фоновые задачи)
- In-App Update