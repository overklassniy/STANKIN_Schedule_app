package com.overklassniy.stankinschedule.migration;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс для работы с настройками расписания версии 1.0.
 * Используется для миграции данных со старой версии приложения.
 */
public class SchedulePreference_1_0 {

    public static final String ROOT_PATH = "schedules";

    private static final String MIGRATE_SCHEDULE = "migrate_schedule";

    private static final String SCHEDULE_PREFERENCE = "schedule_preference";
    private static final String FAVORITE_SCHEDULE = "favorite_schedule";
    private static final String SCHEDULES = "schedules";
    private static final String SUBGROUP = "subgroup";

    private static ArrayList<String> mSchedulesList = null;
    private static String mFavoriteSchedule = null;

    private static long mChangeCount = 0;

    /**
     * Возвращает список названий расписаний.
     *
     * @param context Контекст приложения
     * @return Список названий расписаний
     */
    public static List<String> schedules(@NonNull Context context) {
        if (mSchedulesList == null) {
            load(context);
        }
        return new ArrayList<>(mSchedulesList);
    }

    /**
     * Устанавливает избранное расписание.
     *
     * @param context Контекст приложения
     * @param favoriteSchedule Название избранного расписания
     */
    public static void setFavorite(@NonNull Context context, String favoriteSchedule) {
        if (mFavoriteSchedule == null) {
            load(context);
        }

        mFavoriteSchedule = favoriteSchedule;
        save(context);
    }

    /**
     * Возвращает название избранного расписания.
     *
     * @param context Контекст приложения
     * @return Название избранного расписания
     */
    public static String favorite(@NonNull Context context) {
        if (mFavoriteSchedule == null) {
            load(context);
        }

        return mFavoriteSchedule;
    }

    /**
     * Загружает настройки расписания из SharedPreferences.
     *
     * @param context Контекст приложения
     */
    private static void load(@NonNull Context context) {
        SharedPreferences PREFERENCES =
                context.getSharedPreferences(SCHEDULE_PREFERENCE, Context.MODE_PRIVATE);

        String schedulesString = PREFERENCES.getString(SCHEDULES, "");
        ArrayList<String> schedules = new ArrayList<>();

        if (!schedulesString.isEmpty()) {
            schedules = new ArrayList<>(Arrays.asList(schedulesString.split(";")));
        }

        mSchedulesList = schedules;
        mFavoriteSchedule = PREFERENCES.getString(FAVORITE_SCHEDULE, "");
    }

    /**
     * Сохраняет настройки расписания в SharedPreferences.
     *
     * @param context Контекст приложения
     */
    private static void save(@NonNull Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(SCHEDULE_PREFERENCE, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(SCHEDULES, TextUtils.join(";", mSchedulesList))
                .putString(FAVORITE_SCHEDULE, mFavoriteSchedule)
                .apply();

        ++mChangeCount;
    }

    /**
     * Возвращает список запрещенных символов в названиях расписаний.
     *
     * @return Список запрещенных символов
     */
    public static List<String> banCharacters() {
        return Arrays.asList(";", "/");
    }

    /**
     * Создает полный путь к файлу расписания.
     *
     * @param context Контекст приложения
     * @param scheduleName Название расписания
     * @return Полный путь к файлу расписания
     */
    @NonNull
    public static String createPath(@NonNull Context context, String scheduleName) {
        return new File(scheduleDir(context), scheduleName + fileExtension()).getAbsolutePath();
    }

    /**
     * Возвращает директорию для хранения расписаний.
     *
     * @param context Контекст приложения
     * @return Директория для расписаний
     */
    public static File scheduleDir(@NonNull Context context) {
        return context.getExternalFilesDir(ROOT_PATH);
    }

    /**
     * Возвращает расширение файла для расписаний.
     *
     * @return Расширение файла (".json")
     */
    public static String fileExtension() {
        return ".json";
    }
}