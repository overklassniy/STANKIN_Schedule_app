# Module ui/schedule-core (`:ui:schedule-core`)

Shared UI components for schedules. Contains reusable Compose components for displaying pairs and schedule days, used by other schedule UI modules.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/core/ui/
  PairCard.kt                  - Pair card
  PairColors.kt                - Colors by pair types
  ScheduleDayCard.kt           - Schedule day card
  components/                  - Helper components
```

## Key components

- `PairCard` - Compose pair (class) card component
- `PairColors` - colors for displaying pairs by type
- `ScheduleDayCard` - Compose schedule day card component
- `components/LongClickableText` - text with long click handling
- `components/PairFormatter` - pair text formatter

## Dependencies

- `:ui:core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-settings`

## Technologies

- Jetpack Compose, Material 3
- Hilt (DI)