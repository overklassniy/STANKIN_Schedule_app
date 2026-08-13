# Module data/schedule-table (`:data:schedule-table`)

Data layer for schedule tables. Implements schedule table creation and access to device public storage for export.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/table/data/
  di/                          - Table DI module
  repository/                  - Provider and creator implementations
```

## Key components

### di
- `ScheduleTableModule` - Hilt module: table provider and creator

### repository
- `AndroidPublicProviderImpl` - public storage access implementation
- `AndroidTableCreatorImpl` - schedule table creation implementation

## Dependencies

- `:domain:core`
- `:domain:schedule-table`

## Technologies

- Hilt (DI)