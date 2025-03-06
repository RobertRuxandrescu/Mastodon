package com.example.mastodonfeedapp.di

import com.example.mastodonfeedapp.BuildConfig
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
    @MastodonToken
    fun provideMastodonAccessToken(): String {
        return BuildConfig.MASTODON_ACCESS_TOKEN
    }

}