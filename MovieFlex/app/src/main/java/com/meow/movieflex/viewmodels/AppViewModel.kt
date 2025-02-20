package com.meow.movieflex.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.meow.movieflex.DataState
import com.meow.movieflex.data.repositories.DefaultRepository
import com.meow.movieflex.models.MovieDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: DefaultRepository
) : ViewModel() {

    val movies = Pager(
        PagingConfig(pageSize = 20, prefetchDistance = 5, enablePlaceholders = false)
    ) {
        repository.getMoviesPagingSource()
    }.flow.cachedIn(viewModelScope)

    val tvShows = Pager(
        PagingConfig(pageSize = 20, prefetchDistance = 5, enablePlaceholders = false)
    ) {
        repository.getTvShowsPagingSource()
    }.flow.cachedIn(viewModelScope)

    private val _selectedMovie = MutableStateFlow<DataState>(DataState.Idle)
    val selectedMovie: StateFlow<DataState> = _selectedMovie.asStateFlow()

    fun fetchMovieDetails(movieId: Int) {
        _selectedMovie.value = DataState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMovieDetails(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    result.fold(
                        onSuccess = { details ->
                            _selectedMovie.value = DataState.Success(details)
                        },
                        onFailure = { error ->
                            _selectedMovie.value = DataState.Error(error.message ?: "Failed to load details")
                        }
                    )
                }, { error ->
                    _selectedMovie.value = DataState.Error(error.message ?: "Unknown Error")
                })
        }
    }
}

