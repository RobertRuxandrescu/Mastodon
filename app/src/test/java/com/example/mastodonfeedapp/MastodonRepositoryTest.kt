package com.example.mastodonfeedapp

import com.example.mastodonfeedapp.model.MastodonPost
import com.example.mastodonfeedapp.repository.MastodonRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MastodonRepositoryTest {

    private lateinit var repository: MastodonRepository

    @Before
    fun setup() {
        // Mock the repository
        repository = mock()

        // Mock startStreaming to return a flow with a test post
        whenever(repository.startStreaming()).thenReturn(
            flowOf(
                MastodonPost(
                    id = "123",
                    content = "<p>Hello, Mastodon!</p>"
                )
            )
        )
    }

    @Test
    fun `startStreaming should emit posts received from API`() = runTest {
        // Collect the first emitted post
        val post = repository.startStreaming().first()

        // Validate the emitted data
        assertEquals("123", post.id)
        assertEquals("<p>Hello, Mastodon!</p>", post.content)
    }
}





