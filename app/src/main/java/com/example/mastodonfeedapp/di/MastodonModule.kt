package com.example.mastodonfeedapp.di

import com.example.mastodonfeedapp.repository.MastodonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient

@Module
@InstallIn(ViewModelComponent::class)
object MastodonModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    fun provideMastodonRepository(client: OkHttpClient): MastodonRepository {
        return MastodonRepository(client, "https://mastodon.social")
    }
}