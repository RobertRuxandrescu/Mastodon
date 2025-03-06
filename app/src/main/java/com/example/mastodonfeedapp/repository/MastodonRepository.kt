package com.example.mastodonfeedapp.repository

import com.example.mastodonfeedapp.model.MastodonPost
import kotlinx.coroutines.flow.Flow

interface MastodonRepository {
    fun startStreaming(): Flow<MastodonPost>
}
