# Module ui/journal-predict (`:ui:journal-predict`)

UI module for grade prediction. Contains screen with grade predictions based on current student results.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/journal/predict/ui/
  PredictActivity.kt           - Prediction Activity
  PredictViewModel.kt          - Prediction ViewModel
  components/PredictScreen.kt  - Prediction Compose screen
  model/                       - Models (PredictMark)
  utils/                       - Flow extensions
```

## Key components

- `PredictActivity` - grade prediction Activity
- `PredictViewModel` - grade prediction ViewModel
- `components/PredictScreen` - prediction Compose screen
- `model/PredictMark` - predicted mark model
- `utils/FlowExtentions` - Kotlin Flow extensions

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:journal-core`

## Technologies

- Jetpack Compose, Material 3
- Hilt (DI)