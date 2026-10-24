package com.example.appmodulointermediario.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appmodulointermediario.data.repository.MovieRepository

class ViewModelFactory(
    private val repository: MovieRepository,
    private val movieId: Int = -1
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MovieListViewModel::class.java) -> {
                MovieListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MovieDetailViewModel::class.java) -> {
                MovieDetailViewModel(repository, movieId) as T
            }
            modelClass.isAssignableFrom(MovieFormViewModel::class.java) -> {
                MovieFormViewModel(repository, movieId) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
