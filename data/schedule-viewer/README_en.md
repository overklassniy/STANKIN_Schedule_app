# Module data/schedule-viewer (`:data:schedule-viewer`)

Data layer for schedule viewing. Implements mapping domain models to view models, loading employee data, pagination for schedule viewing.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/viewer/data/
  di/                          - Viewer DI module
  mapper/                      - Pair mapper for display
  repository/                  - Viewer repository implementation
  source/                      - Data sources (employees, schedule)
```

## Key components

### di
- `ScheduleViewerModule` - Hilt module: repository, data sources

### mapper
- `ScheduleViewPairMapper` - mapper from domain pair to view model

### repository
- `ScheduleViewerRepositoryImpl` - schedule viewer repository implementation

### source
- `EmployeeDataSource` - employee data source (API)
- `ScheduleViewerSource` - schedule data source for Paging

## Dependencies

- `:data:core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-viewer`

## Technologies

- Retrofit (network requests)
- Paging (pagination)
- Hilt (DI)