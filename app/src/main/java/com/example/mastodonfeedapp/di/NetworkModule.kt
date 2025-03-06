package com.example.mastodonfeedapp.di

import com.example.mastodonfeedapp.BuildConfig
import com.example.mastodonfeedapp.repository.MastodonRepository
import com.example.mastodonfeedapp.repository.MastodonRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @MastodonToken
    fun provideMastodonAccessToken(): String {
        return BuildConfig.MASTODON_ACCESS_TOKEN
    }

    @Provides
    fun provideMastodonRepository(repository: MastodonRepositoryImpl): MastodonRepository = repository

}