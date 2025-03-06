package com.example.mastodonfeedapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mastodonfeedapp.helpers.NetworkMonitor
import com.example.mastodonfeedapp.repository.MastodonRepository
import com.example.mastodonfeedapp.repository.MastodonRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MastodonViewModel @Inject constructor(
    private val repository: MastodonRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel(), ContainerHost<MastodonState, MastodonSideEffect> {
    override val container = container<MastodonState, MastodonSideEffect>(MastodonState()) {
        observeNetworkChanges()
    }

    private var hasStartedStreaming = false
    private var postLifetime: Long = 10
    private var cleanupJob: Job? = null

    //region PUBLIC FUNCTIONS
    fun setFilterKeyword(keyword: String) = intent {
        reduce { state.copy(filterKeyword = keyword) }

        if (!hasStartedStreaming && keyword.isNotEmpty()) {
            hasStartedStreaming = true
            startStreaming()
        }
    }

    fun onLifetimeEntered(lifetimeInSeconds: Long) = intent {
        postLifetime = lifetimeInSeconds
        startCleanupLoop()
    }
    //endregion

    //region PRIVATE FUNCTIONS
    private fun startStreaming() {
        intent {
            reduce { state.copy(error = null) }
        }
        intent {
            withContext(Dispatchers.IO) {
                repository.startStreaming()
                    .catch { error ->
                        reduce { state.copy(error = error.message) }
                    }
                    .collect { newPost ->
                        val keyword = state.filterKeyword.lowercase()
                        if (newPost.content.lowercase().contains(keyword)) {
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
                if (networkMonitor.isOnline.value) {
                    removeOldMessages()
                }
                delay(TimeUnit.SECONDS.toMillis(postLifetime))
            }
        }
    }

    private fun observeNetworkChanges() {
        intent {
            networkMonitor.isOnline.collect { isOnline ->
                if (isOnline) {
                    startCleanupLoop()
                } else {
                    hasStartedStreaming = false
                    cleanupJob?.cancel()
                }
            }
        }
    }

    private fun removeOldMessages() = intent {
        val currentTime = System.currentTimeMillis() / 1000
        val filteredPosts = state.posts.filter { post ->
            val postAge = currentTime - post.createdAt
            Log.d(
                "MastodonViewModel",
                "Post ID: ${post.id}, Age: $postAge, Lifetime: $postLifetime, Is Old: ${postAge >= postLifetime}"
            )

            postAge < postLifetime
        }

        reduce { state.copy(posts = filteredPosts) }
    }
    //endregion

    override fun onCleared() {
        cleanupJob?.cancel() // Cancel job when ViewModel is destroyed
        super.onCleared()
    }
}