# Module ui/journal-login (`:ui:journal-login`)

UI module for module journal authentication. Contains the OIDC authorization screen for the lko.stankin.ru personal account via WebView (Authorization Code + PKCE) with a seamless sign-in attempt when the SSO session is alive.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/journal/login/ui/
  JournalLoginScreen.kt        - Login Compose screen
  JournalLoginViewModel.kt     - Login ViewModel
  components/                  - Components (WebView, errors, toolbar)
```

## Key components

- `JournalLoginScreen` - journal login Compose screen
- `JournalLoginViewModel` - login ViewModel (silent re-auth, code exchange)
- `components/AuthWebView` - authorization WebView with callback redirect interception
- `components/LoginError` - authentication error display
- `components/LoginToolBar` - login screen toolbar

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:journal-core`

## Technologies

- Jetpack Compose, Material 3
- Hilt (DI)