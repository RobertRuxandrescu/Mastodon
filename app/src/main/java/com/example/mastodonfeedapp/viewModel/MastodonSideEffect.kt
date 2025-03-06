package com.example.mastodonfeedapp.viewModel

sealed class MastodonSideEffect {
    // Not used, left as an example
    data class ShowError(val message: String) : MastodonSideEffect()
}