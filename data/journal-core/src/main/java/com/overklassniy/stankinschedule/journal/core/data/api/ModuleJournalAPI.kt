package com.overklassniy.stankinschedule.journal.core.data.api

import com.overklassniy.stankinschedule.journal.core.data.model.MarkResponse
import com.overklassniy.stankinschedule.journal.core.data.model.SemestersResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Интерфейс API модульного журнала.
 * Используется для взаимодействия с веб-сервисом университета (получение семестров и оценок).
 * Все запросы выполняются методом POST с передачей учетных данных.
 */
interface ModuleJournalAPI {

    /**
     * Получает список доступных семестров для студента.
     *
     * @param login Логин студента (обычно номер зачетной книжки или студенческого билета)
     * @param password Пароль от личного кабинета
     * @return Объект Call, содержащий ответ сервера со списком семестров [SemestersResponse]
     */
    @POST("/webapi/api2/semesters/")
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8")
    @FormUrlEncoded
    fun getSemesters(
        @Field("student") login: String,
        @Field("password") password: String,
    ): Call<SemestersResponse>

    /**
     * Получает оценки (баллы) студента за определенный семестр.
     *
     * @param login Логин студента
     * @param password Пароль от личного кабинета
     * @param semester Идентификатор семестра, за который нужно получить оценки
     * @return Объект Call, содержащий список оценок [MarkResponse]
     */
    @POST("/webapi/api2/marks/")
    @Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8")
    @FormUrlEncoded
    fun getMarks(
        @Field("student") login: String,
        @Field("password") password: String,
        @Field("semester") semester: String,
    ): Call<List<MarkResponse>>
}