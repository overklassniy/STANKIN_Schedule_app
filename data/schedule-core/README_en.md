# Module data/schedule-core (`:data:schedule-core`)

Core data layer for schedules. Implements schedule storage in Room DB, JSON serialization/deserialization, mappers between Entity and domain models, schedule storage repository.

## Directory structure

```
src/main/java/com/overklassniy/stankinschedule/schedule/core/data/
  api/                         - JSON pair model
  db/                          - Room DB (DAO, Entity, Database, ScheduleWithPairs)
  di/                          - DI modules (Room, repositories)
  mapper/                      - Mappers (DateTime, Database, Json, JsonUtils)
  repository/                  - Repository implementations
```

## Key components

### api
- `PairJson` - JSON pair model for serialization

### db
- `ScheduleDao` - Room DAO for schedules and pairs
- `ScheduleDatabase` - Room database for schedules
- `ScheduleEntity` - Room schedule entity
- `PairEntity` - Room pair entity
- `ScheduleWithPairs` - schedule-to-pairs relation

### di
- `ScheduleCoreModule` - Hilt module: mappers, repositories
- `ScheduleDatabaseModule` - Hilt module: Room DB, DAO

### mapper
- `DateTimeConverter` - date/time converter
- `ScheduleDatabaseMapper` - mapper between Entity and domain model
- `ScheduleJsonMapper` - mapper between JSON and domain model
- `ScheduleJsonUtils` - JSON serialization utilities

### repository
- `ScheduleStorageImpl` - schedule storage implementation
- `ScheduleDeviceRepositoryImpl` - device repository implementation

## Dependencies

- `:data:core`
- `:domain:core`
- `:domain:schedule-core`
- `:data:schedule-viewer`

## Technologies

- Room (local DB)
- Retrofit, Gson (JSON)
- Paging (pagination)
- Hilt (DI)