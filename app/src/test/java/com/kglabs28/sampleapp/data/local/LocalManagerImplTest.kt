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
        // Given
        val articles = listOf(testArticle)
        coEvery { dao.insertArticles(articles) } returns Unit

        // When
        localManager.insertArticles(articles)

        // Then
        coVerify { dao.insertArticles(articles) }
    }

    @Test
    fun `getLatestTimestamp should call dao`() = runTest {
        // Given
        coEvery { dao.getNewestTimestamp() } returns 12345L

        // When
        val result = localManager.getLatestTimestamp()

        // Then
        assertEquals(12345L, result)
        coVerify { dao.getNewestTimestamp() }
    }

    @Test
    fun `updateBookmarkStatus should call dao upsert`() = runTest {
        // Given
        coEvery { dao.upsertArticle(testArticle) } returns Unit

        // When
        localManager.updateBookmarkStatus(testArticle)

        // Then
        coVerify { dao.upsertArticle(testArticle) }
    }

    @Test
    fun `searchLocalFeed should call dao`() = runTest {
        // Given
        val query = "android"
        coEvery { dao.searchLocalFeed(query) } returns listOf(testArticle)

        // When
        val result = localManager.searchLocalFeed(query)

        // Then
        assertEquals(listOf(testArticle), result)
        coVerify { dao.searchLocalFeed(query) }
    }

    @Test
    fun `pagingSource should call dao`() {
        // Given
        every { dao.pagingSource() } returns mockk()

        // When
        localManager.pagingSource()

        // Then
        verify { dao.pagingSource() }
    }

    @Test
    fun `enforceCacheLimit should call dao`() = runTest {
        // Given
        val limit = 100
        coEvery { dao.enforceCacheLimit(limit) } returns Unit

        // When
        localManager.enforceCacheLimit(limit)

        // Then
        coVerify { dao.enforceCacheLimit(limit) }
    }

    @Test
    fun `getBookmarkedArticles should return flow from dao`() = runTest {
        // Given
        val articles = listOf(testArticle)
        every { dao.getBookmarkedArticles() } returns flowOf(articles)

        // When & Then
        localManager.getBookmarkedArticles().test {
            assertEquals(articles, awaitItem())
            awaitComplete()
        }
        verify { dao.getBookmarkedArticles() }
    }
}
