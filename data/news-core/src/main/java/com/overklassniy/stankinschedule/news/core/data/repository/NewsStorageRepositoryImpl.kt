package com.overklassniy.stankinschedule.news.core.data.repository

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.overklassniy.stankinschedule.news.core.data.db.NewsDao
import com.overklassniy.stankinschedule.news.core.data.db.NewsDatabase
import com.overklassniy.stankinschedule.news.core.data.mapper.toEntity
import com.overklassniy.stankinschedule.news.core.data.mapper.toPost
import com.overklassniy.stankinschedule.news.core.domain.model.NewsPost
import com.overklassniy.stankinschedule.news.core.domain.repository.NewsStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class NewsStorageRepositoryImpl @Inject constructor(
    private val dbProvider: Provider<NewsDatabase>,
    private val daoProvider: Provider<NewsDao>,
) : NewsStorageRepository {

    private val db: NewsDatabase
        get() = dbProvider.get()

    private val dao: NewsDao
        get() = daoProvider.get()

    override fun news(newsSubdivision: Int): PagingSource<Int, NewsPost> {
        return dao.all(newsSubdivision) // .transform(mapper = { it.toPost() })
    }

    override fun lastNews(newsCount: Int): Flow<List<NewsPost>> {
        return dao.latest(newsCount).map { last -> last.map { it.toPost() } }
    }

    override suspend fun saveNews(newsSubdivision: Int, posts: List<NewsPost>, force: Boolean) {
        db.withTransaction {
            if (force) {
                dao.clear(newsSubdivision)
            }

            val start = dao.nextIndexInResponse(newsSubdivision)

            val news = posts.mapIndexed { index, news ->
                news.toEntity(start + index, newsSubdivision)
            }

            dao.insert(news)
        }
    }

    override suspend fun clearNews(newsSubdivision: Int) {
        db.withTransaction {
            dao.clear(newsSubdivision)
        }
    }
}