package com.example.mastodonfeedapp.viewModel

import com.example.mastodonfeedapp.model.MastodonPost

data class MastodonState(
    val posts: List<MastodonPost> = emptyList(),
    val error: String? = null,
    val filterKeyword: String = ""
)

