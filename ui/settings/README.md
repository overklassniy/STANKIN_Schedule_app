# Модуль ui/settings (`:ui:settings`)

UI-модуль настроек приложения. Содержит экраны настроек: тема приложения (цвета, тёмный режим, язык), настройки расписания, информация о приложении, просмотр Markdown-документов (политика конфиденциальности, условия использования).

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/settings/ui/
  SettingsScreen.kt            - Compose-экран настроек
  SettingsViewModel.kt         - ViewModel настроек
  components/                  - Компоненты (тема, расписание, о приложении)
  theme/                       - Настройки темы (цвета, пресеты)
  utils/                       - Утилиты (Markdown, навигация)
```

## Ключевые компоненты

- `SettingsScreen` - Compose-экран настроек приложения
- `SettingsViewModel` - ViewModel настроек
- `components/AboutAppSection` - раздел «О приложении»
- `components/ScheduleSettingsSection` - раздел настроек расписания
- `components/SettingsSection` - базовый раздел настроек
- `components/SettingsSwitchItem` - элемент-переключатель
- `components/SettingsTextItem` - текстовый элемент настроек
- `components/SettingsToolBar` - тулбар настроек
- `theme/ThemeColorsSection` - раздел настройки цветов темы
- `theme/ThemeDarkModeSection` - раздел настройки тёмной темы
- `theme/ThemeLanguageSection` - раздел настройки языка
- `theme/ThemePresetSection` - раздел выбора пресета темы
- `theme/ThemeScreen` - Compose-экран настройки темы
- `theme/ThemeViewModel` - ViewModel настройки темы
- `utils/MarkdownView` - просмотрщик Markdown
- `utils/SettingsNavigation` - навигация между разделами настроек

## Зависимости

- `:ui:core`
- `:domain:core`
- `:ui:schedule-core`
- `:ui:schedule-widget`
- `:domain:schedule-settings`

## Технологии

- Jetpack Compose, Material 3
- Navigation Compose (навигация)
- WebKit (просмотр Markdown)
- Hilt (DI)