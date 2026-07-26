package com.kglabs28.sampleapp.data.remote

import com.kglabs28.sampleapp.data.remote.dto.ArticleDto
import com.kglabs28.sampleapp.data.remote.dto.NewsResponse
import com.kglabs28.sampleapp.data.remote.dto.SourceDto
import com.kglabs28.sampleapp.utils.BasicUtils
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RemoteManagerImplTest {

    private lateinit var remoteManager: RemoteManagerImpl
    private val newsApi: NewsApi = mockk()

    private val testSourceDto = SourceDto(
        id = "id",
        name = "Source Name",
        url = "source_url",
        country = "US"
    )

    private val testArticleDto = ArticleDto(
        id = "1",
        title = "Title",
        description = "Description",
        content = "Content",
        url = "url",
        image = "image_url",
        publishedAt = "2023-10-27T10:00:00Z",
        source = testSourceDto
    )

    private val testNewsResponse = NewsResponse(
        totalArticles = 1,
        articles = listOf(testArticleDto)
    )

    @Before
    fun setUp() {
        mockkObject(BasicUtils)
        every { BasicUtils.log(any(), any()) } just Runs
        remoteManager = RemoteManagerImpl(newsApi)
    }

    @After
    fun tearDown() {
        unmockkObject(BasicUtils)
    }

    @Test
    fun `getLatestNews should call newsApi and return articles`() = runTest {
        // Given
        val timestamp = "2023-10-27T00:00:00Z"
        coEvery { newsApi.getLatestNews(latestTimeStamp = timestamp) } returns testNewsResponse
        every { BasicUtils.parseTimestampStringToLong(any()) } returns 123456789L

        // When
        val result = remoteManager.getLatestNews(timestamp)

        // Then
        assertEquals(1, result.size)
        assertEquals(testArticleDto.id, result[0].id)
        coVerify { newsApi.getLatestNews(latestTimeStamp = timestamp) }
    }

    @Test
    fun `fetchNews should call newsApi and return articles`() = runTest {
        // Given
        val timestamp = "2023-10-27T00:00:00Z"
        coEvery { newsApi.getMoreNews(oldestTimeStamp = timestamp) } returns testNewsResponse
        every { BasicUtils.parseTimestampStringToLong(any()) } returns 123456789L

        // When
        val result = remoteManager.fetchNews(timestamp)

        // Then
        assertEquals(1, result.size)
        assertEquals(testArticleDto.id, result[0].id)
        coVerify { newsApi.getMoreNews(oldestTimeStamp = timestamp) }
    }

    @Test
    fun `searchNews should call newsApi and return articles`() = runTest {
        // Given
        val query = "android"
        coEvery { newsApi.searchNews(query = query) } returns testNewsResponse
        every { BasicUtils.parseTimestampStringToLong(any()) } returns 123456789L

        // When
        val result = remoteManager.searchNews(query)

        // Then
        assertEquals(1, result.size)
        assertEquals(testArticleDto.id, result[0].id)
        coVerify { newsApi.searchNews(query = query) }
    }
}
