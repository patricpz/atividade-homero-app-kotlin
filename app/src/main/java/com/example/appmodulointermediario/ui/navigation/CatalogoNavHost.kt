package com.example.appmodulointermediario.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appmodulointermediario.data.repository.MovieRepository
import com.example.appmodulointermediario.ui.screens.detail.MovieDetailRoute
import com.example.appmodulointermediario.ui.screens.form.MovieFormRoute
import com.example.appmodulointermediario.ui.screens.list.MovieListRoute
import com.example.appmodulointermediario.viewmodel.MovieDetailViewModel
import com.example.appmodulointermediario.viewmodel.MovieFormViewModel
import com.example.appmodulointermediario.viewmodel.MovieListViewModel
import com.example.appmodulointermediario.viewmodel.ViewModelFactory

@Composable
fun CatalogoNavHost(
    repository: MovieRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MOVIE_LIST,
        modifier = modifier
    ) {
        composable(route = Routes.MOVIE_LIST) {
            val viewModel: MovieListViewModel = viewModel(
                factory = ViewModelFactory(repository)
            )
            MovieListRoute(
                viewModel = viewModel,
                onMovieClick = { movieId ->
                    navController.navigate(Routes.movieDetail(movieId))
                },
                onAddClick = {
                    navController.navigate(Routes.movieForm())
                }
            )
        }

        composable(
            route = Routes.MOVIE_DETAIL,
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: -1
            val viewModel: MovieDetailViewModel = viewModel(
                factory = ViewModelFactory(repository, movieId)
            )
            MovieDetailRoute(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onEditClick = { id ->
                    navController.navigate(Routes.movieForm(id))
                },
                onDeleted = {
                    navController.popBackStack(Routes.MOVIE_LIST, inclusive = false)
                }
            )
        }

        composable(
            route = Routes.MOVIE_FORM,
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: -1
            val viewModel: MovieFormViewModel = viewModel(
                factory = ViewModelFactory(repository, movieId)
            )
            MovieFormRoute(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
