package com.meow.movieflex.api

import com.meow.movieflex.models.Movie
import com.meow.movieflex.models.MovieDetails
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("Watchmode")
    fun provideWatchmodeRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.watchmode.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("TMDb")
    fun provideTmdbRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(@Named("Watchmode") retrofit: Retrofit): WatchmodeApiService =
        retrofit.create(WatchmodeApiService::class.java)

    @Provides
    @Singleton
    fun provideTmdbApiService(@Named("TMDb") retrofit: Retrofit): TmdbApiService =
        retrofit.create(TmdbApiService::class.java)
}
