package com.example.mastodonfeedapp.model

import java.time.Instant

data class MastodonPost(
    val id: String,
    val content: String,
    val createdAt: String
) {
    fun getTimestamp(): Long {
        return Instant.parse(createdAt).epochSecond // Convert to Unix timestamp in seconds
    }
}

