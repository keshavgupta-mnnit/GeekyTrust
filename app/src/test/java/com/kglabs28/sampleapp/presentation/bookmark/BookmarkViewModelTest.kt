package com.kglabs28.sampleapp.presentation.bookmark

import app.cash.turbine.test
import com.kglabs28.sampleapp.MainDispatcherRule
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.usecase.BookmarkUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookmarkViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: BookmarkViewModel
    private val bookmarkUseCase: BookmarkUseCase = mockk()

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
        every { bookmarkUseCase.getAll() } returns flowOf(emptyList())
        viewModel = BookmarkViewModel(bookmarkUseCase)
    }

    @Test
    fun `bookmarks state should emit data from use case`() = runTest {
        // Given
        val articles = listOf(testArticle)
        every { bookmarkUseCase.getAll() } returns flowOf(articles)
        
        // Re-init to capture new flow
        viewModel = BookmarkViewModel(bookmarkUseCase)

        // Then
        viewModel.bookmarks.test {
            assertEquals(articles, awaitItem())
        }
    }

    @Test
    fun `onToggleBookmark should call use case toggle`() = runTest {
        // Given
        coEvery { bookmarkUseCase.toggle(testArticle) } returns Unit

        // When
        viewModel.onToggleBookmark(testArticle)

        // Then
        coVerify { bookmarkUseCase.toggle(testArticle) }
    }
}
