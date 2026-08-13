# Модуль ui/journal-predict (`:ui:journal-predict`)

UI-модуль прогноза оценок. Содержит экран с прогнозом оценок на основе текущих результатов студента.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/journal/predict/ui/
  PredictActivity.kt           - Activity прогноза оценок
  PredictViewModel.kt          - ViewModel прогноза
  components/PredictScreen.kt  - Compose-экран прогноза
  model/                       - Модели (PredictMark)
  utils/                       - Расширения Flow
```

## Ключевые компоненты

- `PredictActivity` - Activity прогноза оценок
- `PredictViewModel` - ViewModel прогноза оценок
- `components/PredictScreen` - Compose-экран прогноза
- `model/PredictMark` - модель прогнозируемой оценки
- `utils/FlowExtentions` - расширения Kotlin Flow

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:journal-core`

## Технологии

- Jetpack Compose, Material 3
- Hilt (DI)