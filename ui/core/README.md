# Модуль ui/core (`:ui:core`)

Базовый UI-слой приложения. Содержит переиспользуемые Compose-компоненты, тему приложения (цвета, размеры), расширения и утилиты, используемые всеми UI-модулями.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/core/ui/
  components/                  - Переиспользуемые UI-компоненты
  components/color/            - Компоненты выбора цвета
  ext/                         - Расширения (Color, Compose, Dimen, String, View)
  notification/                - Утилиты уведомлений
  theme/                       - Тема приложения (Color, Dimen, Theme)
  utils/                       - Утилиты (Browser, Exception, ImageLoader, Formatter)
```

## Ключевые компоненты

### components
- `AppScaffold` - базовый scaffold экрана
- `BackButton` - кнопка навигации назад
- `CalendarDialog` - диалог выбора даты
- `FileSaveDialog` - диалог сохранения файла
- `NotificationUpdateDialog` - диалог обновления уведомлений
- `OutlinedSelectField` - поле выбора с обводкой
- `PagingLazyColumn` - LazyColumn с пагинацией
- `RadioGroup` - группа радио-кнопок
- `Stateful` - обёртка состояния
- `UIState` - модель состояния UI
- `Analytics` - компонент аналитики

### components/color
- `ColorIcon` - иконка цвета
- `ColorPickerDialog` - диалог выбора цвета
- `ColorPreference` - настройка цвета

### ext
- `ColorExtentions` - расширения цвета
- `ComposeExtentions` - расширения Compose
- `DimenExtentions` - расширения размеров
- `IntentExtentions` - расширения Intent
- `StringExtentions` - расширения строк
- `ViewExtentions` - расширения View

### notification
- `NotificationUtils` - утилиты для уведомлений

### theme
- `Color` - цветовая палитра
- `Dimen` - размеры и отступы
- `Theme` - Material 3 тема приложения

### utils
- `BrowserUtils` - утилиты открытия браузера
- `ExceptionDescription` - описание исключений
- `NewsImageLoader` - загрузчик изображений новостей
- `SemesterNameFormatter` - форматировщик названия семестра

## Зависимости

- `:domain:core`

## Технологии

- Jetpack Compose, Material 3
- Coil (загрузка изображений)
- Accompanist (разрешения)
- Paging (пагинация)
- ColorPicker (выбор цвета)
- Hilt (DI)