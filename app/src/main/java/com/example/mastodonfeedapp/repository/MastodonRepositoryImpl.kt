package com.example.mastodonfeedapp.repository

import com.example.mastodonfeedapp.di.MastodonToken
import com.example.mastodonfeedapp.model.MastodonPost
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MastodonRepositoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    @MastodonToken private val accessToken: String
) : MastodonRepository {

    override fun startStreaming(): Flow<MastodonPost> = callbackFlow {
        val request = Request.Builder()
            .url("https://streaming.mastodon.social/api/v1/streaming/public")
            .header(
                "Authorization",
                "Bearer $accessToken"
            ) // access token is held in gradle.properties(Global)
            .build()

        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                if (type == "update") {
                    val jsonObject = JSONObject(data)
                    val newPost = MastodonPost(
                        id = jsonObject.getString("id"),
                        content = jsonObject.getString("content"),
                    )
                    trySend(newPost)
                }
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?
            ) {
                close(t)
            }
        }

        val eventSource = EventSources.createFactory(okHttpClient).newEventSource(request, listener)

        // Ensure event source is properly closed when flow collection stops
        awaitClose { eventSource.cancel() }
    }
}