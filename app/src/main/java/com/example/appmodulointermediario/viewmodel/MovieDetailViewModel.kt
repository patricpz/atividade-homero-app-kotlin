package com.example.appmodulointermediario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmodulointermediario.data.local.MovieEntity
import com.example.appmodulointermediario.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val movie: MovieEntity? = null,
    val isLoading: Boolean = true,
    val isNotFound: Boolean = false,
    val isDeleted: Boolean = false
)

class MovieDetailViewModel(
    private val repository: MovieRepository,
    private val movieId: Int
) : ViewModel() {

    private val _isDeleted = MutableStateFlow(false)

    val uiState: StateFlow<MovieDetailUiState> = repository.getMovieById(movieId)
        .combine(_isDeleted) { movie, isDeleted ->
            MovieDetailUiState(
                movie = movie,
                isLoading = false,
                isNotFound = movie == null,
                isDeleted = isDeleted
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MovieDetailUiState(isLoading = true)
        )

    fun deleteMovie() {
        val currentMovie = uiState.value.movie ?: return
        viewModelScope.launch {
            repository.delete(currentMovie)
            _isDeleted.value = true
        }
    }
}
