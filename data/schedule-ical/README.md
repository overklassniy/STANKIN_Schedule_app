# Модуль data/schedule-ical (`:data:schedule-ical`)

Data-слой экспорта расписания в iCal-формат. Реализует репозиторий для конвертации доменных моделей расписания в iCal-события с использованием библиотеки ical4j.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/ical/data/
  di/                          - DI-модуль iCal
  repository/                  - Реализация репозитория экспорта
```

## Ключевые компоненты

### di
- `ICalModule` - Hilt-модуль: репозиторий iCal

### repository
- `ICalRepository` - реализация экспорта расписания в iCal-формат

## Зависимости

- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-ical`

## Технологии

- ical4j (iCal-генерация)
- Hilt (DI)