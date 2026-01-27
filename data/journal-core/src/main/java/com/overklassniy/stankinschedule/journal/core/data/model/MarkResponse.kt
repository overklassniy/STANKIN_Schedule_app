package com.overklassniy.stankinschedule.journal.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа сервера, содержащая информацию об оценке.
 * Используется для десериализации JSON ответа с данными об успеваемости.
 *
 * @param factor Коэффициент (вес) оценки (например, для курсовых или экзаменов)
 * @param title Название дисциплины или модуля
 * @param type Тип оценки (номер модуля, зачет, экзамен и т.д.), приходит в поле "num"
 * @param value Значение оценки (количество баллов)
 */
class MarkResponse(
    @SerializedName("factor") val factor: Double,
    @SerializedName("title") val title: String,
    @SerializedName("num") val type: String,
    @SerializedName("value") val value: Int,
) {
    /**
     * Возвращает строковое представление объекта.
     * Используется для отладки и логирования.
     *
     * @return Строка с значениями всех полей
     */
    override fun toString(): String {
        return "MarkResponse(factor=$factor, title='$title', type='$type', value=$value)"
    }
}