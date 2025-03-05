package com.example.mastodonfeedapp.viewModel

import androidx.lifecycle.ViewModel
import com.example.mastodonfeedapp.repository.MastodonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@HiltViewModel
class MastodonViewModel(private val repository: MastodonRepository) : ViewModel(), ContainerHost<MastodonState, MastodonSideEffect> {
    override val container = container<MastodonState, MastodonSideEffect>(MastodonState())

    fun startStreaming() {
        // TODO Future call to the repository to fetch the data
    }

    fun setFilterKeyword() {
        // TODO Use this method to filter the feed by user's input
    }
}