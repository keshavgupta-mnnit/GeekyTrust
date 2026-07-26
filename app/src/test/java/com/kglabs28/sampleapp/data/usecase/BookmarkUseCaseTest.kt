package com.kglabs28.sampleapp.data.usecase

import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.repository.NewsRepository
import com.kglabs28.sampleapp.utils.BasicUtils
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class BookmarkUseCaseTest {

    private lateinit var bookmarkUseCase: BookmarkUseCase
    private val repository: NewsRepository = mockk()

    private val testArticle = Article(
        id = "1",
        title = "Title",
        description = "Desc",
        content = "Content",
        imageUrl = "url",
        url = "url",
        sourceName = "Source",
        lastUpdated = 123456789L,
        bookmarkedAt = null
    )

    @Before
    fun setUp() {
        mockkObject(BasicUtils)
        every { BasicUtils.log(any(), any()) } just Runs
        bookmarkUseCase = BookmarkUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkObject(BasicUtils)
    }

    @Test
    fun `getAll should return flow of bookmarked articles`() = runTest {
        // Given
        val articles = listOf(testArticle.copy(bookmarkedAt = 123L))
        every { repository.getBookmarks() } returns flowOf(articles)

        // When
        val result = bookmarkUseCase.getAll()

        // Then
        result.collect {
            assertEquals(articles, it)
        }
        coVerify { repository.getBookmarks() }
    }

    @Test
    fun `toggle should add bookmark if not bookmarked`() = runTest {
        // Given
        val article = testArticle.copy(bookmarkedAt = null)
        val articleSlot = slot<Article>()
        coEvery { repository.updateBookmark(capture(articleSlot)) } returns Unit

        // When
        bookmarkUseCase.toggle(article)

        // Then
        coVerify { repository.updateBookmark(any()) }
        assertNotNull(articleSlot.captured.bookmarkedAt)
        assertEquals(article.id, articleSlot.captured.id)
    }

    @Test
    fun `toggle should remove bookmark if already bookmarked`() = runTest {
        // Given
        val article = testArticle.copy(bookmarkedAt = 123456789L)
        val articleSlot = slot<Article>()
        coEvery { repository.updateBookmark(capture(articleSlot)) } returns Unit

        // When
        bookmarkUseCase.toggle(article)

        // Then
        coVerify { repository.updateBookmark(any()) }
        assertNull(articleSlot.captured.bookmarkedAt)
        assertEquals(article.id, articleSlot.captured.id)
    }
}
