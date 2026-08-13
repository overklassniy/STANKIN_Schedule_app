# Модуль domain/schedule-parser (`:domain:schedule-parser`)

Domain-слой парсера расписаний. Содержит доменные модели парсинга (результат, границы ячеек, настройки), интерфейс PDF-репозитория, use case для извлечения пар и парсинга.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/parser/domain/
  exceptions/                  - Исключения парсинга
  model/                       - Доменные модели парсинга
  repository/                  - Интерфейс PDF-репозитория
  usecase/                     - Use case парсинга
```

## Ключевые компоненты

### exceptions
- `PDFParseException` - исключение парсинга PDF

### model
- `ParseResult` - результат парсинга
- `ParseDetail` - детальная информация о парсинге
- `CellBound` - границы ячейки таблицы
- `TimeCellBound` - границы ячейки времени
- `ParserSettings` - настройки парсера

### repository
- `PDFRepository` - интерфейс репозитория парсинга PDF

### usecase
- `ParserUseCase` - use case парсинга
- `PairExtractor` - извлекатель пар из PDF

## Зависимости

- `:domain:core`
- `:domain:schedule-core`

## Технологии

- Kotlin Coroutines
- Hilt (DI)