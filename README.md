<p align="center">
  <img src=".info/hero.svg" width="100%" alt="СТАНКИН Расписание — неофициальное приложение для студентов МГТУ СТАНКИН: расписание, журнал, новости">
</p>

<p align="center">
  <a href="README.md">Русский</a> · <a href=".info/docs/README_en.md">English</a>
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.overklassniy.stankinschedule"><img src="https://img.shields.io/badge/Google%20Play-Скачать-8cf041?style=for-the-badge&labelColor=grey&logo=googleplay" height="40" alt="Google Play"></a>
  <a href="https://www.rustore.ru/catalog/app/com.overklassniy.stankinschedule"><img src="https://img.shields.io/badge/RuStore-Скачать-red?style=for-the-badge&logo=android" height="40" alt="RuStore"></a>
  <a href="https://github.com/overklassniy/STANKIN_Schedule_app/releases/latest"><img src="https://img.shields.io/badge/GitHub-Release-blue?style=for-the-badge&logo=github" height="40" alt="GitHub Release"></a>
</p>

---

## Скриншоты

<div align="center">

| | | |
|:---:|:---:|:---:|
| <img src=".info/screenshots/main.png" width="260" alt="Главная"> | <img src=".info/screenshots/schedule_day.png" width="260" alt="Расписание"> | <img src=".info/screenshots/modules.png" width="260" alt="Журнал"> |
| <img src=".info/screenshots/schedules.png" width="260" alt="Списки расписаний"> | <img src=".info/screenshots/widget.png" width="260" alt="Виджет"> | |

</div>

---

## Возможности

- **Расписание** — навигация по дням, импорт из ЭОС МГТУ «СТАНКИН»
- **Модульный журнал** — оценки и рейтинг
- **Новости** — университет, деканат, анонсы
- **Виджеты** — расписание и новости на рабочем столе

---

## Технологии

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.4.10-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Material_3-6750A4?style=flat-square&logo=materialdesign&logoColor=white" alt="Material 3">
  <img src="https://img.shields.io/badge/Hilt-4285F4?style=flat-square&logoColor=white" alt="Hilt">
  <img src="https://img.shields.io/badge/Room-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Room">
  <img src="https://img.shields.io/badge/Retrofit-48B883?style=flat-square&logo=retrofit&logoColor=white" alt="Retrofit">
  <img src="https://img.shields.io/badge/Coroutines-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Coroutines">
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black" alt="Firebase">
  <img src="https://img.shields.io/badge/Glance-4285F4?style=flat-square&logo=android&logoColor=white" alt="Glance">
  <img src="https://img.shields.io/badge/Sentry-362D59?style=flat-square&logo=sentry&logoColor=white" alt="Sentry">
</p>

---

## Разработка

<details>
<summary>Требования и сборка</summary>

**Требования:** Android Studio Otter+, JDK 25, Android SDK 37, min API 26

```bash
git clone https://github.com/overklassniy/STANKIN_Schedule_app.git
cd STANKIN_Schedule_app
./gradlew assembleDebug
```

**Sentry DSN** — в `local.properties` укажите ключ для краш-репортинга:
```bash
# local.properties (файл в .gitignore)
sentry.dsn=
```

**RSS-новости** — создайте `stankin.secret` из примера:
```bash
cp stankin.secret.example stankin.secret
# Укажите URL RSS-лент в stankin.secret (файл в .gitignore)
```

Без этих файлов приложение собирается и запускается: Sentry будет отключён, раздел новостей — пустым.

</details>

---

## История изменений

**v3.3.0** — миграция на DataStore, переписывание экранов на Compose, обновление зависимостей

Полный список — [CHANGELOG.md](CHANGELOG.md)

---

## Авторы

- Оригинальный проект — [Nikololoshka](https://github.com/Nikololoshka) (ProjectPepega)
- Текущий форк — [overklassniy](https://github.com/overklassniy)

Неофициальное приложение. Предоставляется «как есть» без каких-либо гарантий.

---

<p align="center">
  <strong>Сделано студентами — для студентов СТАНКИН</strong>
</p>
