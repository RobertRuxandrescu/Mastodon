package com.example.mastodonfeedapp

import com.example.mastodonfeedapp.helpers.NetworkMonitor
import com.example.mastodonfeedapp.model.MastodonPost
import com.example.mastodonfeedapp.repository.MastodonRepositoryImpl
import com.example.mastodonfeedapp.viewModel.MastodonViewModel
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.test.*
import java.time.Instant
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class MastodonViewModelTest {

    private lateinit var viewModel: MastodonViewModel
    private lateinit var repository: MastodonRepositoryImpl
    private lateinit var networkMonitor: NetworkMonitor

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testScope = TestScope()

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        networkMonitor = mockk(relaxed = true)

        every { networkMonitor.isOnline } returns MutableStateFlow(true)

        viewModel = MastodonViewModel(repository, networkMonitor)

    }

    @Test
    fun `old messages should be removed when cleanup loop runs`() = testScope.runTest {
        val oldPost = MastodonPost(id = "1", content = "Old Post", createdAt = Instant.now().minusSeconds(60).epochSecond)
        val newPost = MastodonPost(id = "2", content = "New Post", createdAt = Instant.now().minusSeconds(5).epochSecond)

        viewModel.test(this) {
            runOnCreate()
            expectInitialState()

            // Add the two posts
            containerHost.intent {
                reduce { state.copy(posts = listOf(oldPost, newPost)) }
            }

            // Check the two added posts
            expectState {
                copy(posts = listOf(oldPost, newPost))
            }

            viewModel.setFilterKeyword("Post")
            viewModel.onLifetimeEntered(30)

            advanceTimeBy(TimeUnit.SECONDS.toMillis(31))
            advanceUntilIdle()

            // Check that the old post was removed
            expectState {
                copy(posts = listOf(newPost), filterKeyword = "Post")
            }
        }
    }
}







