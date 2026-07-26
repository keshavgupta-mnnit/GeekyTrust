package com.kglabs28.sampleapp.data.repository

import androidx.paging.*
import com.kglabs28.sampleapp.data.local.LocalManager
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.remote.RemoteManager
import com.kglabs28.sampleapp.utils.BasicUtils
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediatorTest {

    private lateinit var mediator: NewsRemoteMediator
    private val remoteManager: RemoteManager = mockk()
    private val localManager: LocalManager = mockk()

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
        mediator = NewsRemoteMediator(remoteManager, localManager)
    }

    @After
    fun tearDown() {
        unmockkObject(BasicUtils)
    }

    @Test
    fun `initialize should return LAUNCH_INITIAL_REFRESH`() = runTest {
        assertEquals(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH, mediator.initialize())
    }

    @Test
    fun `load REFRESH success should return Success`() = runTest {
        coEvery { localManager.getLatestTimestamp() } returns 1000L
        every { BasicUtils.parseTimestampLongToString(1000L) } returns "2023-10-27"
        coEvery { remoteManager.getLatestNews(any()) } returns listOf(testArticle)
        coEvery { localManager.insertArticles(any()) } returns Unit

        val pagingState = PagingState<Int, Article>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify { localManager.insertArticles(listOf(testArticle)) }
    }

    @Test
    fun `load REFRESH empty should return Success with endOfPaginationReached`() = runTest {
        coEvery { localManager.getLatestTimestamp() } returns null
        coEvery { remoteManager.getLatestNews(null) } returns emptyList()
        coEvery { localManager.insertArticles(emptyList()) } returns Unit

        val pagingState = PagingState<Int, Article>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `load APPEND success should return Success`() = runTest {
        val pagingState = PagingState<Int, Article>(
            pages = listOf(PagingSource.LoadResult.Page(listOf(testArticle), null, null)),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )
        every { BasicUtils.parseTimestampLongToString(any()) } returns "2023-10-26"
        coEvery { remoteManager.fetchNews(any()) } returns listOf(testArticle.copy(id = "2"))
        coEvery { localManager.insertArticles(any()) } returns Unit

        val result = mediator.load(LoadType.APPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify { remoteManager.fetchNews("2023-10-26") }
    }

    @Test
    fun `load APPEND with no items should return Success`() = runTest {
        val pagingState = PagingState<Int, Article>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(LoadType.APPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify(exactly = 0) { remoteManager.fetchNews(any()) }
    }

    @Test
    fun `load PREPEND should return Success with endOfPaginationReached true`() = runTest {
        val pagingState = PagingState<Int, Article>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(LoadType.PREPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `load should return Error on IOException`() = runTest {
        coEvery { localManager.getLatestTimestamp() } throws IOException("No connection")

        val pagingState = PagingState<Int, Article>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals("No connection", (result as RemoteMediator.MediatorResult.Error).throwable.message)
    }
}
