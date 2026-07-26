package com.kglabs28.sampleapp.presentation.news

import app.cash.turbine.test
import com.kglabs28.sampleapp.MainDispatcherRule
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.usecase.BookmarkUseCase
import com.kglabs28.sampleapp.data.usecase.GetNewsUseCase
import com.kglabs28.sampleapp.utils.BasicUtils
import io.mockk.*
import androidx.paging.PagingData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NewsViewModel
    private val getNewsUseCase: GetNewsUseCase = mockk()
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
        mockkObject(BasicUtils)
        every { BasicUtils.log(any(), any()) } just Runs
        every { getNewsUseCase.execute() } returns flowOf(PagingData.from(listOf(testArticle)))
        
        viewModel = NewsViewModel(getNewsUseCase, bookmarkUseCase)
    }

    @After
    fun tearDown() {
        unmockkObject(BasicUtils)
    }

    @Test
    fun `initial news flow should trigger execute when collected`() = runTest {
        val job = launch {
            viewModel.news.collect {}
        }
        
        advanceTimeBy(600)
        
        coVerify { getNewsUseCase.execute() }
        job.cancel()
    }

    @Test
    fun `onSearchQueryChange should trigger search after debounce`() = runTest {
        val query = "android"
        coEvery { getNewsUseCase.search(query) } returns listOf(testArticle)

        val job = launch {
            viewModel.news.collect {}
        }

        viewModel.onSearchQueryChange(query)
        
        advanceTimeBy(600)

        coVerify { getNewsUseCase.search(query) }
        job.cancel()
    }

    @Test
    fun `onToggleBookmark should call use case toggle`() = runTest {
        coEvery { bookmarkUseCase.toggle(testArticle) } returns Unit

        viewModel.onToggleBookmark(testArticle)

        coVerify { bookmarkUseCase.toggle(testArticle) }
    }
}
