package com.kglabs28.sampleapp.data.usecase

import androidx.paging.PagingData
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.repository.NewsRepository
import com.kglabs28.sampleapp.utils.BasicUtils
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetNewsUseCaseTest {

    private lateinit var getNewsUseCase: GetNewsUseCase
    private val repository: NewsRepository = mockk()

    @Before
    fun setUp() {
        mockkObject(BasicUtils)
        every { BasicUtils.log(any(), any()) } just Runs
        getNewsUseCase = GetNewsUseCase(repository)
    }

    @After
    fun tearDown() {
        unmockkObject(BasicUtils)
    }

    @Test
    fun `execute should return paging data from repository`() = runTest {
        // Given
        val pagingData = PagingData.empty<Article>()
        every { repository.getNews() } returns flowOf(pagingData)

        // When
        val result = getNewsUseCase.execute()

        // Then
        result.collect {
            assertEquals(pagingData, it)
        }
        coVerify { repository.getNews() }
    }

    @Test
    fun `search should return list of articles from repository`() = runTest {
        // Given
        val query = "android"
        val articles = listOf(
            Article(
                id = "1",
                title = "Title",
                description = "Desc",
                content = "Content",
                imageUrl = "url",
                url = "url",
                sourceName = "Source",
                lastUpdated = 123456789L
            )
        )
        coEvery { repository.searchNews(query) } returns articles

        // When
        val result = getNewsUseCase.search(query)

        // Then
        assertEquals(articles, result)
        coVerify { repository.searchNews(query) }
    }
}
