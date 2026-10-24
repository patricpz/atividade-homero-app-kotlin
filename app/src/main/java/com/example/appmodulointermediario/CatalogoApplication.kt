package com.example.appmodulointermediario

import android.app.Application
import com.example.appmodulointermediario.data.local.AppDatabase
import com.example.appmodulointermediario.data.repository.MovieRepository

class CatalogoApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: MovieRepository by lazy { MovieRepository(database.movieDao()) }
}
