package com.overklassniy.stankinschedule.journal.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа сервера, содержащая информацию о студенте и списке доступных семестров.
 * Используется для первичной авторизации и получения данных о студенте.
 *
 * @param surname Фамилия студента
 * @param initials Инициалы студента
 * @param group Название учебной группы (поле "stgroup" в JSON)
 * @param semesters Список доступных семестров (обычно в формате "2023-осень")
 */
class SemestersResponse(
    @SerializedName("surname") val surname: String,
    @SerializedName("initials") val initials: String,
    @SerializedName("stgroup") val group: String,
    @SerializedName("semesters") val semesters: List<String>,
) {
    /**
     * Возвращает строковое представление объекта.
     * Используется для отладки и логирования.
     *
     * @return Строка с данными студента и списком семестров
     */
    override fun toString(): String {
        return "SemestersResponse(surname='$surname', initials='$initials', group='$group', semesters=$semesters)"
    }
}