package com.example.mastodonfeedapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mastodonfeedapp.repository.MastodonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MastodonViewModel @Inject constructor(
    private val repository: MastodonRepository
) : ViewModel(), ContainerHost<MastodonState, MastodonSideEffect> {
    override val container = container<MastodonState, MastodonSideEffect>(MastodonState())

    private var hasStartedStreaming = false
    private var postLifetime: Int? = null
    private var cleanupJob: Job? = null

    fun setFilterKeyword(keyword: String) = intent {
        reduce { state.copy(filterKeyword = keyword) }

        if (!hasStartedStreaming && keyword.isNotEmpty()) {
            hasStartedStreaming = true
            startStreaming()
        }
    }

    fun onLifetimeEntered(lifetimeInSeconds: Int) = intent {
        postLifetime = lifetimeInSeconds
        startCleanupLoop()
    }

    private fun startStreaming() {
        viewModelScope.launch {
            repository.startStreaming()
                .catch { error ->
                    intent {
                        reduce { state.copy(error = error.message) }
                        postSideEffect(MastodonSideEffect.ShowError(error.message ?: "Error while getting the data"))
                    }
                }
                .collect { newPost ->
                    intent {
                        val keyword = state.filterKeyword.lowercase()
                        if (keyword.isEmpty() || newPost.content.lowercase().contains(keyword)) {
                            reduce { state.copy(posts = state.posts + newPost) }
                        }
                    }
                }
        }
    }

    private fun startCleanupLoop() {
        cleanupJob?.cancel()
        cleanupJob = viewModelScope.launch {
            while (isActive) {
                delay(TimeUnit.SECONDS.toMillis(5))
                removeOldMessages() // Continuously remove old posts
            }
        }
    }

    private fun removeOldMessages() = intent {
        val currentTime = System.currentTimeMillis() / 1000 // Get current time in seconds
        val filteredPosts = state.posts.filter { post ->
            postLifetime?.let { currentTime - post.getTimestamp() < it } ?: true
        }
        reduce { state.copy(posts = filteredPosts) }
    }

    override fun onCleared() {
        cleanupJob?.cancel() // Cancel job when ViewModel is destroyed
        super.onCleared()
    }
}