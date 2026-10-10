<p align="center">
  <img src="../hero_en.svg" width="100%" alt="STANKIN Schedule — unofficial app for MSTU STANKIN students: schedule, journal, news">
</p>

<p align="center">
  <a href="../../README.md">Русский</a> · <a href="README_en.md">English</a>
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.overklassniy.stankinschedule"><img src="https://img.shields.io/badge/Google%20Play-Download-8cf041?style=for-the-badge&labelColor=grey&logo=googleplay" height="40" alt="Google Play"></a>
  <a href="https://www.rustore.ru/catalog/app/com.overklassniy.stankinschedule"><img src="https://img.shields.io/badge/RuStore-Download-red?style=for-the-badge&logo=android" height="40" alt="RuStore"></a>
  <a href="https://github.com/overklassniy/STANKIN_Schedule_app/releases/latest"><img src="https://img.shields.io/badge/GitHub-Release-blue?style=for-the-badge&logo=github" height="40" alt="GitHub Release"></a>
</p>

---

## Screenshots

<div align="center">

| | | |
|:---:|:---:|:---:|
| <img src="../screenshots/main.png" width="260" alt="Main"> | <img src="../screenshots/schedule_day.png" width="260" alt="Schedule"> | <img src="../screenshots/modules.png" width="260" alt="Journal"> |
| <img src="../screenshots/schedules.png" width="260" alt="Schedule List"> | <img src="../screenshots/widget.png" width="260" alt="Widget"> | |

</div>

---

## Features

- **Schedule** — day navigation, import from STANKIN Moodle
- **Module Journal** — grades and ratings
- **News** — university, dean's office, announcements
- **Widgets** — schedule and news on the home screen

---

## Technologies

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

## Development

<details>
<summary>Requirements and build</summary>

**Requirements:** Android Studio Otter+, JDK 25, Android SDK 37, min API 26

```bash
git clone https://github.com/overklassniy/STANKIN_Schedule_app.git
cd STANKIN_Schedule_app
./gradlew assembleDebug
```

**Sentry DSN** — add your crash reporting key to `local.properties`:
```bash
# local.properties (file is in .gitignore)
sentry.dsn=
```

**RSS news** — create `stankin.secret` from the example:
```bash
cp stankin.secret.example stankin.secret
# Add your RSS feed URLs to stankin.secret (file is in .gitignore)
```

Without these files, the app builds and runs: Sentry will be disabled, news section will be empty.

</details>

---

## Changelog

**v3.4.0** — module journal migrated to the new lko.stankin.ru personal account

Full changelog — [CHANGELOG_en.md](CHANGELOG_en.md)

---

## Authors

- Original project — [Nikololoshka](https://github.com/Nikololoshka) (ProjectPepega)
- Current fork — [overklassniy](https://github.com/overklassniy)

Unofficial application. Provided "as is" without any warranties.

---

<p align="center">
  <strong>Made by students — for STANKIN students</strong>
</p>
