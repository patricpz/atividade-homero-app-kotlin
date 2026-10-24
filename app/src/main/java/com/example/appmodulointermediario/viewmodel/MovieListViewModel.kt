package com.example.appmodulointermediario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmodulointermediario.data.local.MovieEntity
import com.example.appmodulointermediario.data.repository.MovieRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MovieListUiState(
    val movies: List<MovieEntity> = emptyList()
)

class MovieListViewModel(repository: MovieRepository) : ViewModel() {
    val uiState: StateFlow<MovieListUiState> = repository.allMovies
        .map { MovieListUiState(movies = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MovieListUiState()
        )
}
