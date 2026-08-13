# Модуль ui/schedule-core (`:ui:schedule-core`)

Общие UI-компоненты расписания. Содержит переиспользуемые Compose-компоненты для отображения пар и дней расписания, используемые другими UI-модулями расписания.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/core/ui/
  PairCard.kt                  - Карточка пары
  PairColors.kt               - Цвета по типам пар
  ScheduleDayCard.kt          - Карточка дня расписания
  components/                  - Вспомогательные компоненты
```

## Ключевые компоненты

- `PairCard` - Compose-компонент карточки пары (занятия)
- `PairColors` - цвета для отображения пар по типам
- `ScheduleDayCard` - Compose-компонент карточки дня расписания
- `components/LongClickableText` - текст с обработкой долгого нажатия
- `components/PairFormatter` - форматировщик текста пары

## Зависимости

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-settings`

## Технологии

- Jetpack Compose, Material 3
- Hilt (DI)