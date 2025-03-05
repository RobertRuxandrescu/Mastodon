package com.example.mastodonfeedapp.viewModel

sealed class MastodonSideEffect {
    data class ShowError(val message: String) : MastodonSideEffect()
}