package com.example.mastodonfeedapp.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class MastodonPost(
    val id: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis() / 1000
) {
    fun convertTimestampToDate(timestamp: Long): String {
        // Convert Unix timestamp to LocalDateTime in the system's default time zone
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())

        // Define the format for the date
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss") // Example format: "Mar 06, 2025 16:22:11"

        // Format the date
        return dateTime.format(formatter)
    }
}
