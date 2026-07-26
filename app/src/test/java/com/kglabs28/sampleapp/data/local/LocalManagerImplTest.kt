package com.kglabs28.sampleapp.data.local

import com.kglabs28.sampleapp.data.local.db.NewsDatabase
import com.kglabs28.sampleapp.data.local.db.dao.NewsArticleDao
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.utils.BasicUtils
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test

class LocalManagerImplTest {

    private lateinit var localManager: LocalManagerImpl
    private val database: NewsDatabase = mockk()
    private val dao: NewsArticleDao = mockk()

    private val testArticle = Article(
        id = "1",
        title = "Title",
        description = "Desc",
        content = "Content",
        imageUrl = "url",
        url = "url",
        sourceName = "Source",
        lastUpdated = 123456789L
    )

    @Before
    fun setUp() {
        mockkObject(BasicUtils)
        every { BasicUtils.log(any(), any()) } just Runs
        every { database.newsArticleDao } returns dao
        localManager = LocalManagerImpl(database)
    }

    @After
    fun tearDown() {
        unmockkObject(BasicUtils)
    }

    @Test
    fun `insertArticles should call dao`() = runTest {
        val articles = listOf(testArticle)
        coEvery { dao.insertArticles(articles) } returns Unit

        localManager.insertArticles(articles)

        coVerify { dao.insertArticles(articles) }
    }

    @Test
    fun `getLatestTimestamp should call dao`() = runTest {
        coEvery { dao.getNewestTimestamp() } returns 12345L

        val result = localManager.getLatestTimestamp()

        assertEquals(12345L, result)
        coVerify { dao.getNewestTimestamp() }
    }

    @Test
    fun `updateBookmarkStatus should call dao upsert`() = runTest {
        coEvery { dao.upsertArticle(testArticle) } returns Unit

        localManager.updateBookmarkStatus(testArticle)

        coVerify { dao.upsertArticle(testArticle) }
    }

    @Test
    fun `searchLocalFeed should call dao`() = runTest {
        val query = "android"
        coEvery { dao.searchLocalFeed(query) } returns listOf(testArticle)

        val result = localManager.searchLocalFeed(query)

        assertEquals(listOf(testArticle), result)
        coVerify { dao.searchLocalFeed(query) }
    }

    @Test
    fun `pagingSource should call dao`() {
        every { dao.pagingSource() } returns mockk()

        localManager.pagingSource()

        verify { dao.pagingSource() }
    }

    @Test
    fun `enforceCacheLimit should call dao`() = runTest {
        val limit = 100
        coEvery { dao.enforceCacheLimit(limit) } returns Unit

        localManager.enforceCacheLimit(limit)

        coVerify { dao.enforceCacheLimit(limit) }
    }

    @Test
    fun `getBookmarkedArticles should return flow from dao`() = runTest {
        val articles = listOf(testArticle)
        every { dao.getBookmarkedArticles() } returns flowOf(articles)

        localManager.getBookmarkedArticles().test {
            assertEquals(articles, awaitItem())
            awaitComplete()
        }
        verify { dao.getBookmarkedArticles() }
    }
}
