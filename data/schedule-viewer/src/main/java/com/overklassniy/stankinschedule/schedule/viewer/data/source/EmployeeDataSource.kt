package com.overklassniy.stankinschedule.schedule.viewer.data.source

import android.content.Context
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
            context.assets.open("employee/academic_deps_employee.csv").use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { reader ->
                    val lines = reader.readLines()
                    if (lines.size < 2) return emptyMap()

                    val result = mutableMapOf<String, EmployeeInfo>()
                    for (i in 1 until lines.size) {
                        val parts = lines[i].split(";")
                        if (parts.size >= 7) {
                            val fullName = parts[3].trim()
                            val shortName = parts[4].trim()
                            val departmentsStr = parts[5].trim()
                            val departments = departmentsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            val email = parts[6].trim()

                            if (shortName.isNotEmpty()) {
                                result[shortName] = EmployeeInfo(
                                    fullName = fullName,
                                    shortName = shortName,
                                    departments = departments,
                                    email = email,
                                )
                            }
                        }
                    }
                    result
                }
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}