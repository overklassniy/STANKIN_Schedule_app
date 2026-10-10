# Модуль ui/schedule-widget (`:ui:schedule-widget`)

UI-модуль виджета расписания. Содержит экран настройки виджета и Glance-виджет для отображения расписания на домашнем экране, с обновлением через WorkManager.

## Структура каталогов

```
src/main/java/com/overklassniy/stankinschedule/schedule/widget/ui/
  components/                  - Компоненты экрана настройки
  configure/                   - Activity и ViewModel настройки
  glance/                      - Glance-виджет и его компоненты
  utils/                       - Утилиты (deep link)
```

## Ключевые компоненты

- `components/ScheduleWidgetConfigureScreen` - Compose-экран настройки виджета
- `components/ScheduleWidgetConfigureAppBar` - тулбар настройки виджета
- `configure/ScheduleWidgetConfigureActivity` - Activity настройки виджета
- `configure/ScheduleWidgetConfigureViewModel` - ViewModel настройки
- `glance/ScheduleGlanceWidget` - Glance-виджет расписания
- `glance/ScheduleGlanceWidgetReceiver` - получатель виджета
- `glance/ScheduleGlanceEntryPoint` - точка входа Glance
- `glance/ScheduleWidgetUpdater` - обновление виджета
- `glance/WidgetStateKeys` - ключи состояния виджета
- `utils/ScheduleDeepLink` - deep link для открытия расписания из виджета

## Зависимости

- `:ui:core`
- `:ui:schedule-core`
- `:data:core`
- `:data:schedule-widget`
- `:domain:core`
- `:domain:schedule-core`
- `:domain:schedule-widget`
- `:domain:schedule-settings`

## Технологии

- Jetpack Compose, Material 3
- Glance (виджет)
- Hilt (DI)

## Примечание об архитектуре

Модуль намеренно симметричен `:ui:news-widget`: структура экранов настройки,
Glance-компонентов и state-ключей повторяет виджет новостей со спецификой расписания.
Это архитектурное решение — симметрия упрощает поддержку парных виджетов, поэтому
дублирование между ними (находки jscpd) не устраняется объединением модулей.