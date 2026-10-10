# Модуль ui/journal-login (`:ui:journal-login`)

UI-модуль авторизации в модульный журнал. Содержит экран OIDC-авторизации в личном кабинете lko.stankin.ru через WebView (Authorization Code + PKCE) с бесшовной попыткой входа при живой SSO-сессии.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/journal/login/ui/
  JournalLoginScreen.kt        - Compose-экран авторизации
  JournalLoginViewModel.kt     - ViewModel авторизации
  components/                  - Компоненты (WebView, ошибки, тулбар)
```

## Ключевые компоненты

- `JournalLoginScreen` - Compose-экран авторизации в журнал
- `JournalLoginViewModel` - ViewModel авторизации (silent re-auth, обмен кода на токен)
- `components/AuthWebView` - WebView авторизации с перехватом callback-редиректа
- `components/LoginError` - отображение ошибки авторизации
- `components/LoginToolBar` - тулбар экрана авторизации

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:journal-core`

## Технологии

- Jetpack Compose, Material 3
- Hilt (DI)