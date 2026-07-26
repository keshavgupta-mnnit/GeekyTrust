package com.kglabs28.sampleapp.data.repository

import android.content.Context
import com.kglabs28.sampleapp.data.local.LocalManager
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.remote.RemoteManager
import com.kglabs28.sampleapp.utils.BasicUtils
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test

class NewsRepositoryImplTest {

    private lateinit var repository: NewsRepositoryImpl
    private val context: Context = mockk()
    private val localManager: LocalManager = mockk()
    private val remoteManager: RemoteManager = mockk()

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
        repository = NewsRepositoryImpl(context, localManager, remoteManager)
    }

    @After
    fun tearDown() {
        unmockkObject(BasicUtils)
    }

    @Test
    fun `searchNews should call remoteManager when online`() = runTest {
        // Given
        val query = "android"
        every { BasicUtils.isNetworkConnected(context) } returns true
        coEvery { remoteManager.searchNews(query) } returns listOf(testArticle)

        // When
        val result = repository.searchNews(query)

        // Then
        assertEquals(listOf(testArticle), result)
        coVerify { remoteManager.searchNews(query) }
        coVerify(exactly = 0) { localManager.searchLocalFeed(any()) }
    }

    @Test
    fun `searchNews should call localManager when offline`() = runTest {
        // Given
        val query = "android"
        every { BasicUtils.isNetworkConnected(context) } returns false
        coEvery { localManager.searchLocalFeed(query) } returns listOf(testArticle)

        // When
        val result = repository.searchNews(query)

        // Then
        assertEquals(listOf(testArticle), result)
        coVerify { localManager.searchLocalFeed(query) }
        coVerify(exactly = 0) { remoteManager.searchNews(any()) }
    }

    @Test
    fun `updateBookmark should call localManager`() = runTest {
        // Given
        coEvery { localManager.updateBookmarkStatus(testArticle) } returns Unit

        // When
        repository.updateBookmark(testArticle)

        // Then
        coVerify { localManager.updateBookmarkStatus(testArticle) }
    }

    @Test
    fun `getBookmarks should call localManager`() = runTest {
        // Given
        val articles = listOf(testArticle)
        every { localManager.getBookmarkedArticles() } returns flowOf(articles)

        // When & Then
        repository.getBookmarks().test {
            assertEquals(articles, awaitItem())
            awaitComplete()
        }
        verify { localManager.getBookmarkedArticles() }
    }
}
