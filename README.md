<div align="center">

# СТАНКИН Расписание

**Неофициальное приложение для студентов МГТУ «СТАНКИН»**

[![Version](https://img.shields.io/badge/version-3.2.3-blue.svg)](https://github.com/overklassniy/STANKIN_Schedule_app/releases/latest)
[![Android](https://img.shields.io/badge/Android-8.0%2B-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-purple.svg)](https://kotlinlang.org/)
\
<img src=".info/icons/224x224.png" width="224" alt="СТАНКИН Расписание">

[🇷🇺 Русский](README.md) • [🇺🇸 English](.info/docs/README_en.md)

</div>

---

## О проекте

**СТАНКИН Расписание** – это неофициальное мобильное приложение для студентов МГТУ «СТАНКИН», которое предоставляет удобный доступ к расписанию занятий, модульному журналу и новостям университета прямо с вашего смартфона.

Этот проект является форком [ProjectPepega](https://github.com/Nikololoshka/ProjectPepega) – оригинального приложения, которое больше не поддерживается автором. Проект был возобновлен и продолжает развиваться с улучшениями и новыми функциями.

### Основные возможности

- **Расписание занятий** – просмотр расписания с удобной навигацией по дням
- **Модульный журнал** – доступ к оценкам и рейтингу
- **Новости университета** – свежие новости прямо в приложении
- **Виджет на рабочем столе** – быстрый доступ к расписанию без открытия приложения
- **Темная тема** – комфортная работа в любое время суток
- **Импорт расписаний** – поддержка импорта из различных источников, включая Электронную Образовательную Среду (ЭОС) МГТУ «СТАНКИН»

## Скачать

<div align="center">

[<img src="https://img.shields.io/badge/Google%20Play-Скачать-8cf041?style=for-the-badge&labelColor=grey&logo=googleplay" height="60">](https://play.google.com/store/apps/details?id=com.overklassniy.stankinschedule)
[<img src="https://img.shields.io/badge/RuStore-Скачать-red?style=for-the-badge&logo=android" height="60">](https://www.rustore.ru/catalog/app/com.overklassniy.stankinschedule)
[<img src="https://img.shields.io/badge/GitHub-Release-blue?style=for-the-badge&logo=github" height="60">](https://github.com/overklassniy/STANKIN_Schedule_app/releases/latest)

</div>

---

## Скриншоты

<div align="center">

| Главная | Расписание | Журнал |
|:---:|:---:|:---:|
| <img src=".info/screenshots/main.png" width="300" alt="Главная"> | <img src=".info/screenshots/schedule_day.png" width="300" alt="Расписание"> | <img src=".info/screenshots/modules.png" width="300" alt="Журнал"> |

| Списки расписаний | Виджет |
|:---:|:---:|
| <img src=".info/screenshots/schedules.png" width="300" alt="Списки расписаний"> | <img src=".info/screenshots/widget.png" width="300" alt="Виджет"> |

</div>

---

## Технологии

Приложение построено на современных технологиях Android-разработки:

- **Kotlin 2.3.0** – современный язык программирования
- **Jetpack Compose** – декларативный UI-фреймворк
- **Material Design 3** – современный дизайн-язык
- **Hilt** – dependency injection
- **Room** – локальная база данных
- **Retrofit** – работа с сетевыми запросами
- **Firebase** – аналитика и crash reporting
- **Coroutines** – асинхронное программирование

## Разработка

### Требования

- Android Studio Otter или новее
- JDK 21
- Android SDK 36
- Минимальная версия Android: 8.0 (API 26)

### Сборка проекта

```bash
# Клонировать репозиторий
git clone https://github.com/overklassniy/STANKIN_Schedule_app.git
cd STANKIN_Schedule_app

# Собрать проект
./gradlew assembleDebug

# Или открыть в Android Studio
```

### Новости (RSS)

```bash
cp stankin.secret.example stankin.secret
# Отредактируйте stankin.secret и укажите реальные URL (файл уже в .gitignore)
```

Без `stankin.secret` приложение собирается и запускается, раздел новостей будет пустым.

---

## История изменений

Полный список изменений доступен в [CHANGELOG.md](changelog.md).

### Последнее обновление

**Версия 3.2.3 (17.03.26)**
- Добавлен функционал просмотра информации о преподавателе: полное ФИО, кафедры и электронная почта @stankin.ru
- Исправлены ссылки на новости в зависимости от выбранной вкладки новостей
- Исправлено неверное открытие выбранной даты в календаре на некоторых устройствах

## Авторы

- **Оригинальный проект** – [Nikololoshka](https://github.com/Nikololoshka) (ProjectPepega)
- **Текущий форк** – [overklassniy](https://github.com/overklassniy)

## Отказ от ответственности

Это неофициальное приложение. Приложение предоставляется "как есть" без каких-либо гарантий.

---

<div align="center">

**Сделано студентами – для студентов СТАНКИН**

</div>