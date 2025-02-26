package com.meow.cosmos.di

import com.meow.cosmos.data.repositories.ChatRepository
import com.meow.cosmos.data.usecases.ChatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindChatRepository(impl:ChatRepositoryImpl): ChatRepository
}