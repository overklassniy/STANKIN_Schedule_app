# Module data/schedule-parser (`:data:schedule-parser`)

Data layer for PDF schedule parsing. Implements repository for extracting pairs from PDF files using the PDFBox library.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/parser/data/
  di/                          - Parser DI module
  repository/                  - PDF repository implementation
```

## Key components

### di
- `ParserModule` - Hilt module: PDF repository

### repository
- `PDFRepositoryImpl` - PDF schedule parsing repository implementation

## Dependencies

- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-parser`

## Technologies

- PDFBox (PDF parsing)
- Hilt (DI)