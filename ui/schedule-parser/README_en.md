# Module ui/schedule-parser (`:ui:schedule-parser`)

UI module for PDF schedule parsing. Contains a 5-step wizard screen: file selection, parser settings, parsing, saving, finish. Includes step navigation and error handling.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/parser/ui/
  ScheduleParserActivity.kt    - Parser Activity
  ScheduleParserScreen.kt      - Parser Compose screen
  ScheduleParserViewModel.kt   - Parser ViewModel
  components/                  - Components (stepper, navigation, dialogs)
  forms/                       - Wizard forms (select, settings, save)
  model/                       - State models
  util/                        - Utilities (error mapper)
```

## Key components

- `ScheduleParserActivity` - PDF parser Activity
- `ScheduleParserScreen` - Compose parsing wizard screen
- `ScheduleParserViewModel` - parser ViewModel
- `components/CircleProgressStepper` - circular step indicator
- `components/LineProgressStepper` - linear step indicator
- `components/StepperNavigation` - step navigation
- `components/ScheduleParserAppBar` - parser toolbar
- `components/ReadPermissionDeniedDialog` - permission denial dialog
- `forms/SelectForm` - file selection form
- `forms/SettingsForm` - parser settings form
- `forms/ParserForm` - parsing process form
- `forms/SaveForm` - save form
- `forms/FinishForm` - finish form
- `model/ParserState` - parser state
- `model/ParsedFile` - parsed file model
- `model/SelectedFile` - selected file model
- `model/SaveScheduleError` - save error
- `util/ParserErrorMapper` - parsing error mapper

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-parser`

## Technologies

- Jetpack Compose, Material 3
- Hilt (DI)