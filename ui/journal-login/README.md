# Модуль ui/journal-login (`:ui:journal-login`)

UI-модуль авторизации в модульный журнал. Содержит экран ввода учётных данных студента с обработкой ошибок авторизации.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/journal/login/ui/
  JournalLoginScreen.kt        - Compose-экран авторизации
  JournalLoginViewModel.kt     - ViewModel авторизации
  components/                  - Компоненты (ошибки, тулбар)
```

## Ключевые компоненты

- `JournalLoginScreen` - Compose-экран авторизации в журнал
- `JournalLoginViewModel` - ViewModel авторизации
- `components/LoginError` - отображение ошибки авторизации
- `components/LoginToolBar` - тулбар экрана авторизации

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:journal-core`

## Технологии

- Jetpack Compose, Material 3
- Hilt (DI)