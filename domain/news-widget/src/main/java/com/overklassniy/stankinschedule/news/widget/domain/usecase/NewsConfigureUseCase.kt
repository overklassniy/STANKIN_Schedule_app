package com.overklassniy.stankinschedule.news.widget.domain.usecase

import com.overklassniy.stankinschedule.news.core.domain.model.NewsSubdivision
import javax.inject.Inject

/**
 * UseCase для конфигурирования виджета новостей.
 *
 * Предоставляет список всех доступных категорий новостей для выбора в конфигураторе.
 * Загрузка и сохранение конфигурации виджета выполняются через Glance state
 * на уровне ViewModel, так как Glance API требует Android-контекста.
 */
class NewsConfigureUseCase @Inject constructor() {

    /**
     * Возвращает список всех доступных категорий новостей.
     *
     * @return Список [NewsSubdivision].
     */
    fun subdivisions(): List<NewsSubdivision> = NewsSubdivision.entries.toList()
}