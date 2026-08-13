# Module ui/settings (`:ui:settings`)

UI module for app settings. Contains settings screens: app theme (colors, dark mode, language), schedule settings, about app, Markdown document viewer (privacy policy, terms of use).

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/settings/ui/
  SettingsScreen.kt            - Settings Compose screen
  SettingsViewModel.kt         - Settings ViewModel
  components/                  - Components (theme, schedule, about)
  theme/                       - Theme settings (colors, presets)
  utils/                       - Utilities (Markdown, navigation)
```

## Key components

- `SettingsScreen` - app settings Compose screen
- `SettingsViewModel` - settings ViewModel
- `components/AboutAppSection` - about app section
- `components/ScheduleSettingsSection` - schedule settings section
- `components/SettingsSection` - base settings section
- `components/SettingsSwitchItem` - switch settings item
- `components/SettingsTextItem` - text settings item
- `components/SettingsToolBar` - settings toolbar
- `theme/ThemeColorsSection` - theme colors settings section
- `theme/ThemeDarkModeSection` - dark mode settings section
- `theme/ThemeLanguageSection` - language settings section
- `theme/ThemePresetSection` - theme preset selection section
- `theme/ThemeScreen` - theme settings Compose screen
- `theme/ThemeViewModel` - theme settings ViewModel
- `utils/MarkdownView` - Markdown viewer
- `utils/SettingsNavigation` - navigation between settings sections

## Dependencies

- `:ui:core`
- `:domain:core`
- `:ui:schedule-core`
- `:ui:schedule-widget`
- `:domain:schedule-settings`

## Technologies

- Jetpack Compose, Material 3
- Navigation Compose (navigation)
- WebKit (Markdown viewing)
- Hilt (DI)