package com.kglabs28.sampleapp.presentation.detail

import com.kglabs28.sampleapp.MainDispatcherRule
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.usecase.BookmarkUseCase
import com.kglabs28.sampleapp.utils.BasicUtils
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DetailViewModel
    private val bookmarkUseCase: BookmarkUseCase = mockk()

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
        
        viewModel = DetailViewModel(mockk(relaxed = true), bookmarkUseCase)
        
        val field = viewModel.javaClass.getDeclaredField("_article")
        field.isAccessible = true
        (field.get(viewModel) as MutableStateFlow<Article>).value = testArticle
    }

    @After
    fun tearDown() {
        unmockkObject(BasicUtils)
    }

    @Test
    fun `viewModel should have correct article after injection`() {
        assertEquals(testArticle, viewModel.article.value)
    }

    @Test
    fun `toggleBookmark should call use case and update local state`() = runTest {
        coEvery { bookmarkUseCase.toggle(any()) } returns Unit

        viewModel.toggleBookmark()

        coVerify { bookmarkUseCase.toggle(any()) }
        assertNotNull(viewModel.article.value.bookmarkedAt)
    }
}
