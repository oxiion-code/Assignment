package com.meow.movieflex.ui.screens.description

import ShimmerEffectDetails
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meow.movieflex.DataState
import com.meow.movieflex.R
import com.meow.movieflex.models.MovieDetails
import com.meow.movieflex.viewmodels.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(movieId: Int, viewModel: AppViewModel, onBackPress: () -> Unit, innerPaddingValues: PaddingValues) {
    val context = LocalContext.current
    val selectedMovieState by viewModel.selectedMovie.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.fetchMovieDetails(movieId)
    }

    when (selectedMovieState) {
        is DataState.Loading -> {
            ShimmerEffectDetails(innerPaddingValues = innerPaddingValues)
        }

        is DataState.Success<*> -> {
            val movieDetails = (selectedMovieState as? DataState.Success<MovieDetails>)?.data

            if (movieDetails == null) {
                // Handle case where movie details are unexpectedly null
                Text(
                    text = "Movie details not available.",
                    modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
                return
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(movieDetails.title ?: "Unknown Title", color = Color.Black, fontWeight = FontWeight.Bold)
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackPress) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.Black
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(padding)
                ) {
                    // Image with fallback
                    AsyncImage(
                        model = movieDetails.backdrop ?: movieDetails.posterLarge ?:"https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg", // Use placeholder
                        contentDescription = movieDetails.title ?: "Movie Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = movieDetails.title ?: "Title Unavailable",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Release Date: ${movieDetails.release_date ?: "N/A"}",
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Rating: ${movieDetails.user_rating?.toString() ?: "N/A"}/10",
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = movieDetails.plot_overview ?: "No description available.",
                            color = Color(0xFF616161)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Type: ${movieDetails.type ?: "Unknown"}",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        is DataState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Failed to load details",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (selectedMovieState as DataState.Error).message,
                    color = Color.Gray
                )
            }
            Toast.makeText(context, "Error: ${(selectedMovieState as DataState.Error).message}", Toast.LENGTH_SHORT).show()
        }

        else -> {}
    }
}

