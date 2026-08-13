# Module data/schedule-widget (`:data:schedule-widget`)

Data layer for the schedule widget. Provides implementation of the schedule widget preferences repository for the Glance widget.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/widget/data/
  di/                          - Schedule widget DI module
  repository/                  - Preferences repository implementation
```

## Key components

### di
- `ScheduleWidgetModule` - Hilt module: widget preferences repository

### repository
- `ScheduleWidgetPreferenceImpl` - schedule widget preferences repository implementation

## Dependencies

- `:domain:core`
- `:domain:schedule-widget`

## Technologies

- DataStore (preferences)
- Hilt (DI)