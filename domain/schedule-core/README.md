# Модуль domain/schedule-core (`:domain:schedule-core`)

Domain-слой расписания. Содержит основные доменные модели (пара, расписание, дата, время, типы), интерфейсы репозиториев (хранилище, устройство) и use case для работы с парами и расписаниями.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/core/domain/
  exceptions/                  - Исключения расписания
  model/                       - Доменные модели
  repository/                  - Интерфейсы репозиториев
  usecase/                     - Use case расписания
```

## Ключевые компоненты

### exceptions
- `ScheduleException` - исключения расписания

### model
- `PairModel` - модель пары (занятия)
- `PairInfo` - информация о паре
- `ScheduleModel` - модель расписания
- `ScheduleInfo` - информация о расписании
- `ScheduleDayModel` - модель дня расписания
- `DateModel`, `DateSingle`, `DateRange`, `DateItem` - модели дат
- `DayOfWeek` - день недели
- `Time` - время пары
- `Type` - тип занятия (лекция, практика, лабораторная)
- `Subgroup` - подгруппа
- `Frequency` - частота проведения

### repository
- `ScheduleStorage` - интерфейс хранилища расписаний
- `ScheduleDeviceRepository` - интерфейс репозитория устройства

### usecase
- `ScheduleUseCase` - use case работы с расписанием
- `PairUseCase` - use case работы с парами
- `ScheduleDeviceUseCase` - use case устройства

## Зависимости

- `:domain:core`

## Технологии

- Kotlin Coroutines
- Paging (пагинация)
- Hilt (DI)