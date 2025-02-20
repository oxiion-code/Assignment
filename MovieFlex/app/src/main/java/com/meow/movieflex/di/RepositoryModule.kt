package com.meow.movieflex.di
import com.meow.movieflex.data.implementaion.DefaultRepositoryImpl
import com.meow.movieflex.data.repositories.DefaultRepository
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
    abstract fun bindAuthRepository(impl: DefaultRepositoryImpl): DefaultRepository
}