package com.example.appmodulointermediario.data.repository

import com.example.appmodulointermediario.data.local.MovieDao
import com.example.appmodulointermediario.data.local.MovieEntity
import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieDao: MovieDao) {
    val allMovies: Flow<List<MovieEntity>> = movieDao.getAll()

    fun getMovieById(id: Int): Flow<MovieEntity?> {
        return movieDao.getById(id)
    }

    suspend fun insert(movie: MovieEntity): Long {
        return movieDao.insert(movie)
    }

    suspend fun update(movie: MovieEntity) {
        movieDao.update(movie)
    }

    suspend fun delete(movie: MovieEntity) {
        movieDao.delete(movie)
    }
}
