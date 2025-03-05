package com.example.mastodonfeedapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mastodonfeedapp.repository.MastodonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MastodonViewModel @Inject constructor(
    private val repository: MastodonRepository
) : ViewModel(), ContainerHost<MastodonState, MastodonSideEffect> {
    override val container = container<MastodonState, MastodonSideEffect>(MastodonState())

    private var hasStartedStreaming = false

    fun setFilterKeyword(keyword: String) = intent {
        reduce { state.copy(filterKeyword = keyword) }

        if (!hasStartedStreaming && keyword.isNotEmpty()) {
            hasStartedStreaming = true
            startStreaming()
        }
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
}