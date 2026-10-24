package com.example.appmodulointermediario.ui.navigation

object Routes {
    const val MOVIE_LIST = "movies"
    const val MOVIE_DETAIL = "movie/{movieId}"
    const val MOVIE_FORM = "movie/form?movieId={movieId}"

    fun movieDetail(movieId: Int): String = "movie/$movieId"
    fun movieForm(movieId: Int = -1): String = "movie/form?movieId=$movieId"
}
