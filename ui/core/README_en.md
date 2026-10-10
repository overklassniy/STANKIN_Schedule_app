# Module ui/core (`:ui:core`)

Base UI layer of the application. Contains reusable Compose components, app theme (colors, dimensions), extensions and utilities used by all UI modules.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/core/ui/
  components/                  - Reusable UI components
  components/color/            - Color picker components
  ext/                         - Extensions (Color, Compose, Dimen, String, View)
  notification/                - Notification utilities
  theme/                       - App theme (Color, Dimen, Theme)
  utils/                       - Utilities (Browser, Exception, ImageLoader, Formatter)
```

## Key components

### components
- `AppScaffold` - base screen scaffold
- `BackButton` - back navigation button
- `CalendarDialog` - date picker dialog
- `FileSaveDialog` - file save dialog
- `NameInputDialog` - name input dialog with error indication (create/rename schedule)
- `NotificationUpdateDialog` - notification update dialog
- `OutlinedSelectField` - outlined select field
- `PagingLazyColumn` - LazyColumn with pagination
- `RadioGroup` - radio button group
- `Stateful` - state wrapper
- `UIState` - UI state model
- `Analytics` - analytics component

### components/color
- `ColorIcon` - color icon
- `ColorPickerDialog` - color picker dialog
- `ColorPreference` - color preference

### ext
- `ColorExtentions` - color extensions
- `ComposeExtentions` - Compose extensions
- `DimenExtentions` - dimension extensions
- `IntentExtentions` - Intent extensions
- `StringExtentions` - string extensions
- `ViewExtentions` - View extensions

### notification
- `NotificationUtils` - notification utilities

### theme
- `AppThemeEntryPoint` - app theme entry point
- `Color` - color palette
- `Dimen` - dimensions and padding
- `Theme` - Material 3 app theme

### utils
- `BrowserUtils` - browser opening utilities
- `ExceptionDescription` - exception descriptions
- `NewsImageLoader` - news image loader
- `SemesterNameFormatter` - semester name formatter

## Dependencies

- `:domain:core`

## Technologies

- Jetpack Compose, Material 3
- Coil (image loading)
- Accompanist (permissions)
- Paging (pagination)
- ColorPicker (color selection)
- Hilt (DI)