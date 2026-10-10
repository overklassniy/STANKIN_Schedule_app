package com.overklassniy.stankinschedule.schedule.viewer.data.source

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

/**
 * Информация о сотруднике из CSV.
 *
 * @property fullName Полное ФИО.
 * @property shortName Фамилия И.О. (для сопоставления с расписанием).
 * @property departments Подразделения (разделитель ", ").
 * @property email E-mail.
 */
data class EmployeeInfo(
    val fullName: String,
    val shortName: String,
    val departments: List<String>,
    val email: String,
) {
    val departmentsDisplay: String
        get() = departments.joinToString(", ")
}

/**
 * Загружает данные сотрудников из CSV (Фамилия;Имя;Отчество;ФИО;Фамилия И.О.;Подразделения;E-mail).
 * Поиск по столбцу "Фамилия И.О.".
 */
class EmployeeDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val cache: Map<String, EmployeeInfo> by lazy { loadEmployees() }

    /**
     * Ищет сотрудника по "Фамилия И.О." (сопоставление с расписанием).
     *
     * @param shortName Строка вида "Новоселова О.В." или "Новоселова О.В".
     * @return [EmployeeInfo] или null, если не найдено.
     */
    fun findByShortName(shortName: String): EmployeeInfo? {
        if (shortName.isBlank()) return null
        val normalized = shortName.trim()
        return cache[normalized]
            ?: cache[normalized.trimEnd('.')]
            ?: cache[normalized + "."]
            ?: cache.entries.find {
                it.key.equals(normalized, ignoreCase = true) ||
                it.key.equals(normalized.trimEnd('.'), ignoreCase = true) ||
                it.key.equals(normalized + ".", ignoreCase = true)
            }?.value
    }

    private fun loadEmployees(): Map<String, EmployeeInfo> {
        return try {
            context.assets.open(EMPLOYEES_FILE).use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    parseEmployees(reader.readLines())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load employees CSV", e)
            emptyMap()
        }
    }

    /**
     * Разбирает строки CSV в карту сотрудников (пропускает заголовок).
     */
    private fun parseEmployees(lines: List<String>): Map<String, EmployeeInfo> {
        return lines.drop(1)
            .mapNotNull { parseEmployee(it) }
            .associateBy { it.shortName }
    }

    /**
     * Разбирает одну строку CSV в [EmployeeInfo] или null при невалидном формате.
     */
    private fun parseEmployee(line: String): EmployeeInfo? {
        val parts = line.split(";")
        if (parts.size < MIN_COLUMNS) return null

        val shortName = parts[COL_SHORT_NAME].trim()
        if (shortName.isEmpty()) return null

        return EmployeeInfo(
            fullName = parts[COL_FULL_NAME].trim(),
            shortName = shortName,
            departments = parts[COL_DEPARTMENTS].split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() },
            email = parts[COL_EMAIL].trim(),
        )
    }

    private companion object {
        const val TAG = "EmployeeDataSource"
        const val EMPLOYEES_FILE = "employee/academic_deps_employee.csv"
        const val MIN_COLUMNS = 7
        const val COL_FULL_NAME = 3
        const val COL_SHORT_NAME = 4
        const val COL_DEPARTMENTS = 5
        const val COL_EMAIL = 6
    }
}
