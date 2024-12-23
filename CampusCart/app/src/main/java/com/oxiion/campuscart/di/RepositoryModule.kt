package com.oxiion.campuscart.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oxiion.campuscart.domain.repository.ProductRepositoryImpl
import com.oxiion.campuscart.domain.repository.AdminRepository
import com.oxiion.campuscart.domain.repository.AdminRepositoryImpl
import com.oxiion.campuscart.domain.repository.CampusManRepository
import com.oxiion.campuscart.domain.repository.CampusManRepositoryImpl
import com.oxiion.campuscart.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAdminAuthRepository(impl: AdminRepositoryImpl): AdminRepository

    @Singleton
    @Binds
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Singleton
    @Binds
    abstract fun bindCampusManRepository(impl:CampusManRepositoryImpl):CampusManRepository
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}