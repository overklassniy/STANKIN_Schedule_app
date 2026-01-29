package com.overklassniy.stankinschedule.schedule.table.domain.usecase

import android.net.Uri
import com.overklassniy.stankinschedule.schedule.core.domain.model.ScheduleModel
import com.overklassniy.stankinschedule.schedule.table.domain.model.TableConfig
import com.overklassniy.stankinschedule.schedule.table.domain.repository.AndroidPublicProvider
import com.overklassniy.stankinschedule.schedule.table.domain.repository.AndroidTableCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * UseCase для генерации и экспорта таблицы расписания.
 *
 * Предоставляет методы создания и сохранения PDF/изображений, а также
 * создания URI для последующей отправки.
 */
class AndroidTableUseCase @Inject constructor(
    private val provider: AndroidPublicProvider,
    private val creator: AndroidTableCreator
) {

    /**
     * Сохраняет изображение таблицы расписания в указанный [Uri].
     *
     * @param schedule Модель расписания.
     * @param config Конфигурация таблицы.
     * @param uri Целевой URI для сохранения.
     * @return Поток, который эмитит итоговый [Uri] после сохранения.
     */
    fun saveImageTable(
        schedule: ScheduleModel,
        config: TableConfig,
        uri: Uri
    ): Flow<Uri> = flow {
        val bitmap = creator.createImage(schedule, config)
        provider.exportBitmap(bitmap, uri)
        emit(uri)
    }.flowOn(Dispatchers.IO)

    /**
     * Сохраняет PDF с таблицей расписания в указанный [Uri].
     *
     * @param schedule Модель расписания.
     * @param config Конфигурация таблицы.
     * @param uri Целевой URI для сохранения.
     * @return Поток, который эмитит [Uri] после сохранения.
     */
    fun savePdfTable(
        schedule: ScheduleModel,
        config: TableConfig,
        uri: Uri
    ): Flow<Uri> = flow {
        val pdf = creator.createPdf(schedule, config)
        provider.exportPdf(pdf, uri)
        emit(uri)
    }.flowOn(Dispatchers.IO)

    /**
     * Создаёт [Uri] для PDF-документа с таблицей расписания.
     *
     * @param name Имя файла.
     * @param schedule Модель расписания.
     * @param config Конфигурация таблицы.
     * @return Поток с созданным [Uri].
     */
    fun createUriForPdf(
        name: String,
        schedule: ScheduleModel,
        config: TableConfig,
    ): Flow<Uri> = flow {
        val pdf = creator.createPdf(schedule, config)
        val uri = provider.createUri(name, pdf)
        emit(uri)
    }.flowOn(Dispatchers.IO)

    /**
     * Создаёт [Uri] для изображения (Bitmap) таблицы расписания.
     *
     * @param name Имя файла.
     * @param schedule Модель расписания.
     * @param config Конфигурация таблицы.
     * @return Поток с созданным [Uri].
     */
    fun createUriForImage(
        name: String,
        schedule: ScheduleModel,
        config: TableConfig,
    ): Flow<Uri> = flow {
        val bitmap = creator.createImage(schedule, config)
        val uri = provider.createUri(name, bitmap)
        emit(uri)
    }.flowOn(Dispatchers.IO)
}