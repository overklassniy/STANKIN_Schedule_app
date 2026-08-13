# Модуль data/schedule-viewer (`:data:schedule-viewer`)

Data-слой просмотра расписания. Реализует маппинг доменных моделей в модели для отображения, загрузку данных о преподавателях, пагинацию для просмотра расписания.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/viewer/data/
  di/                          - DI-модуль просмотра
  mapper/                      - Маппер пар для отображения
  repository/                  - Реализация репозитория просмотра
  source/                      - Источники данных (сотрудники, расписание)
```

## Ключевые компоненты

### di
- `ScheduleViewerModule` - Hilt-модуль: репозиторий, источники данных

### mapper
- `ScheduleViewPairMapper` - маппер доменной пары в модель для отображения

### repository
- `ScheduleViewerRepositoryImpl` - реализация репозитория просмотра расписания

### source
- `EmployeeDataSource` - источник данных о преподавателях (API)
- `ScheduleViewerSource` - источник данных расписания для Paging

## Зависимости

- `:data:core`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-viewer`

## Технологии

- Retrofit (сетевые запросы)
- Paging (пагинация)
- Hilt (DI)