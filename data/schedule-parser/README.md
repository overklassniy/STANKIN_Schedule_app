# Модуль data/schedule-parser (`:data:schedule-parser`)

Data-слой парсинга PDF-расписаний. Реализует репозиторий для извлечения пар из PDF-файлов с использованием библиотеки PDFBox.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/parser/data/
  di/                          - DI-модуль парсера
  repository/                  - Реализация PDF-репозитория
```

## Ключевые компоненты

### di
- `ParserModule` - Hilt-модуль: PDF-репозиторий

### repository
- `PDFRepositoryImpl` - реализация репозитория парсинга PDF-расписаний

## Зависимости

- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-parser`

## Технологии

- PDFBox (парсинг PDF)
- Hilt (DI)