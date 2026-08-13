# Module domain/schedule-parser (`:domain:schedule-parser`)

Domain layer for schedule parsing. Contains parsing domain models (result, cell bounds, settings), PDF repository interface, use cases for pair extraction and parsing.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/parser/domain/
  exceptions/                  - Parsing exceptions
  model/                       - Parsing domain models
  repository/                  - PDF repository interface
  usecase/                     - Parsing use cases
```

## Key components

### exceptions
- `PDFParseException` - PDF parsing exception

### model
- `ParseResult` - parsing result
- `ParseDetail` - detailed parsing info
- `CellBound` - table cell bounds
- `TimeCellBound` - time cell bounds
- `ParserSettings` - parser settings

### repository
- `PDFRepository` - PDF parsing repository interface

### usecase
- `ParserUseCase` - parsing use case
- `PairExtractor` - pair extractor from PDF

## Dependencies

- `:domain:core`
- `:domain:schedule-core`

## Technologies

- Kotlin Coroutines
- Hilt (DI)