# Module data/schedule-ical (`:data:schedule-ical`)

Data layer for schedule export to iCal format. Implements repository for converting domain schedule models to iCal events using the ical4j library.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/ical/data/
  di/                          - iCal DI module
  repository/                  - Export repository implementation
```

## Key components

### di
- `ICalModule` - Hilt module: iCal repository

### repository
- `ICalRepository` - schedule export to iCal format implementation

## Dependencies

- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-ical`

## Technologies

- ical4j (iCal generation)
- Hilt (DI)