# Модуль ui/schedule-parser (`:ui:schedule-parser`)

UI-модуль парсера PDF-расписаний. Содержит экран-мастер из 5 шагов: выбор файла, настройка парсера, парсинг, сохранение, завершение. Включает пошаговую навигацию и обработку ошибок.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/parser/ui/
  ScheduleParserActivity.kt    - Activity парсера
  ScheduleParserScreen.kt      - Compose-экран парсера
  ScheduleParserViewModel.kt   - ViewModel парсера
  components/                  - Компоненты (степпер, навигация, диалоги)
  forms/                       - Формы мастера (выбор, настройки, сохранение)
  model/                       - Модели состояния
  util/                        - Утилиты (маппер ошибок)
```

## Ключевые компоненты

- `ScheduleParserActivity` - Activity парсера PDF
- `ScheduleParserScreen` - Compose-экран мастера парсинга
- `ScheduleParserViewModel` - ViewModel парсера
- `components/CircleProgressStepper` - круговой индикатор шагов
- `components/LineProgressStepper` - линейный индикатор шагов
- `components/StepperNavigation` - навигация между шагами
- `components/ScheduleParserAppBar` - тулбар парсера
- `components/ReadPermissionDeniedDialog` - диалог отказа в разрешении
- `forms/SelectForm` - форма выбора файла
- `forms/SettingsForm` - форма настроек парсера
- `forms/ParserForm` - форма процесса парсинга
- `forms/SaveForm` - форма сохранения
- `forms/FinishForm` - форма завершения
- `model/ParserState` - состояние парсера
- `model/ParsedFile` - модель распарсенного файла
- `model/SelectedFile` - модель выбранного файла
- `model/SaveScheduleError` - ошибка сохранения
- `util/ParserErrorMapper` - маппер ошибок парсинга

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-parser`

## Технологии

- Jetpack Compose, Material 3
- Hilt (DI)