package com.example.appmodulointermediario.ui.screens.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appmodulointermediario.R
import com.example.appmodulointermediario.viewmodel.MovieFormUiState
import com.example.appmodulointermediario.viewmodel.MovieFormViewModel

@Composable
fun MovieFormRoute(
    viewModel: MovieFormViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaved()
        }
    }

    MovieFormScreen(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onGenreChange = viewModel::onGenreChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onTypeChange = viewModel::onTypeChange,
        onSaveClick = viewModel::saveMovie,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieFormScreen(
    uiState: MovieFormUiState,
    onTitleChange: (String) -> Unit,
    onGenreChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleRes = if (uiState.isEditMode) {
        R.string.screen_movie_edit_title
    } else {
        R.string.screen_movie_create_title
    }

    val typeMovie = stringResource(R.string.type_movie)
    val typeSeries = stringResource(R.string.type_series)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(titleRes)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_content_desc)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = onTitleChange,
                    label = { Text(text = stringResource(R.string.label_title)) },
                    isError = uiState.titleError,
                    supportingText = if (uiState.titleError) {
                        { Text(text = stringResource(R.string.error_title_required)) }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.genre,
                    onValueChange = onGenreChange,
                    label = { Text(text = stringResource(R.string.label_genre)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text(
                        text = stringResource(R.string.label_type),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterChip(
                            selected = uiState.type == typeMovie,
                            onClick = { onTypeChange(typeMovie) },
                            label = { Text(text = typeMovie) },
                            leadingIcon = if (uiState.type == typeMovie) {
                                { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
                            } else null
                        )

                        FilterChip(
                            selected = uiState.type == typeSeries,
                            onClick = { onTypeChange(typeSeries) },
                            label = { Text(text = typeSeries) },
                            leadingIcon = if (uiState.type == typeSeries) {
                                { Icon(imageVector = Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChange,
                    label = { Text(text = stringResource(R.string.label_description)) },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.button_save))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieFormScreenPreview() {
    MovieFormScreen(
        uiState = MovieFormUiState(
            title = "Inception",
            genre = "Ficção Científica",
            description = "Ladrão que invade sonhos.",
            type = "Filme"
        ),
        onTitleChange = {},
        onGenreChange = {},
        onDescriptionChange = {},
        onTypeChange = {},
        onSaveClick = {},
        onBackClick = {}
    )
}
