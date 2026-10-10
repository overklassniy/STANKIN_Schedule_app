package com.overklassniy.stankinschedule.journal.core.data.api

import com.overklassniy.stankinschedule.journal.core.data.model.EduGroupResponse
import com.overklassniy.stankinschedule.journal.core.data.model.ExternalStudyProgressResponse
import com.overklassniy.stankinschedule.journal.core.data.model.TokenResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Интерфейс API личного кабинета студента (lko-api.stankin.ru).
 * Используется для взаимодействия с веб-сервисом университета
 * (получение успеваемости, данных студента и обмен кода авторизации на токен).
 *
 * Авторизация запросов выполняется Bearer-токеном в заголовке Authorization.
 */
interface ModuleJournalAPI {

    /**
     * Получает данные успеваемости студента.
     *
     * Без параметров возвращает последний доступный период;
     * с параметрами — указанные курс и семестр.
     *
     * @param bearer Заголовок авторизации вида "Bearer <token>".
     * @param course Номер курса (опционально).
     * @param semester Номер семестра внутри курса (опционально).
     * @return Объект Call с данными успеваемости [ExternalStudyProgressResponse].
     */
    @GET("lkstudent/api/externalStudyProgress")
    fun getStudyProgress(
        @Header("Authorization") bearer: String,
        @Query("course") course: Int? = null,
        @Query("semester") semester: Int? = null,
    ): Call<ExternalStudyProgressResponse>

    /**
     * Получает учебную группу студента.
     *
     * @param bearer Заголовок авторизации вида "Bearer <token>".
     * @return Объект Call с данными группы [EduGroupResponse].
     */
    @GET("lkstudent/studedugroup/GetStudEduGroup/")
    fun getEduGroup(
        @Header("Authorization") bearer: String,
    ): Call<EduGroupResponse>

    /**
     * Обменивает код авторизации OIDC на токен доступа.
     *
     * @param code Код авторизации из callback-редиректа.
     * @param clientId Идентификатор клиента личного кабинета.
     * @param codeVerifier PKCE code_verifier, соответствующий code_challenge запроса авторизации.
     * @return Объект Call с токеном [TokenResponse].
     */
    @GET("auth/gettoken")
    fun getToken(
        @Query("code") code: String,
        @Query("clientId") clientId: String,
        @Query("codeVerifier") codeVerifier: String,
    ): Call<TokenResponse>
}
