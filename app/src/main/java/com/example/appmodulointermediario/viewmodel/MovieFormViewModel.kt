package com.example.appmodulointermediario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmodulointermediario.data.local.MovieEntity
import com.example.appmodulointermediario.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieFormUiState(
    val title: String = "",
    val genre: String = "",
    val description: String = "",
    val type: String = "Filme",
    val titleError: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false
)

class MovieFormViewModel(
    private val repository: MovieRepository,
    private val movieId: Int = -1
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MovieFormUiState(isEditMode = movieId > 0, isLoading = movieId > 0)
    )
    val uiState: StateFlow<MovieFormUiState> = _uiState.asStateFlow()

    init {
        if (movieId > 0) {
            loadMovie(movieId)
        }
    }

    private fun loadMovie(id: Int) {
        viewModelScope.launch {
            val movie = repository.getMovieById(id).firstOrNull()
            if (movie != null) {
                _uiState.update { current ->
                    current.copy(
                        title = movie.title,
                        genre = movie.genre,
                        description = movie.description,
                        type = movie.type,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update {
            it.copy(
                title = newTitle,
                titleError = if (it.titleError) newTitle.trim().isEmpty() else false
            )
        }
    }

    fun onGenreChange(newGenre: String) {
        _uiState.update { it.copy(genre = newGenre) }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onTypeChange(newType: String) {
        _uiState.update { it.copy(type = newType) }
    }

    fun saveMovie() {
        val current = _uiState.value
        val trimmedTitle = current.title.trim()
        if (trimmedTitle.isEmpty()) {
            _uiState.update { it.copy(titleError = true) }
            return
        }

        viewModelScope.launch {
            if (current.isEditMode) {
                repository.update(
                    MovieEntity(
                        id = movieId,
                        title = trimmedTitle,
                        genre = current.genre.trim(),
                        description = current.description.trim(),
                        type = current.type
                    )
                )
            } else {
                repository.insert(
                    MovieEntity(
                        title = trimmedTitle,
                        genre = current.genre.trim(),
                        description = current.description.trim(),
                        type = current.type
                    )
                )
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
