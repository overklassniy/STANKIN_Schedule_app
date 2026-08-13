# Module ui/journal-login (`:ui:journal-login`)

UI module for module journal authentication. Contains student credentials input screen with authentication error handling.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/journal/login/ui/
  JournalLoginScreen.kt        - Login Compose screen
  JournalLoginViewModel.kt     - Login ViewModel
  components/                  - Components (errors, toolbar)
```

## Key components

- `JournalLoginScreen` - journal login Compose screen
- `JournalLoginViewModel` - login ViewModel
- `components/LoginError` - authentication error display
- `components/LoginToolBar` - login screen toolbar

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:journal-core`

## Technologies

- Jetpack Compose, Material 3
- Hilt (DI)