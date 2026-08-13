# Module data/schedule-settings (`:data:schedule-settings`)

Data layer for schedule display settings. Implements settings storage (colors by pair types, groups) via DataStore.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/settings/data/
  di/                          - Settings DI module
  repository/                  - DataStore storage implementation
```

## Key components

### di
- `ScheduleSettingsModule` - Hilt module: schedule settings DataStore

### repository
- `ScheduleDataStore` - schedule display settings storage implementation via DataStore

## Dependencies

- `:domain:core`
- `:domain:schedule-settings`

## Technologies

- DataStore (preferences)
- Hilt (DI)