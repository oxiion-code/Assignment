package com.meow.movieflex.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.meow.movieflex.models.Movie
import com.meow.movieflex.viewmodels.AppViewModel
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

val LightPrimaryColor = Color(0xFF424242)
val LightSecondaryColor = Color(0xFF757575)
val LightBackgroundColor = Color(0xFFF5F5F5)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToMovieDetails: (Int) -> Unit,
    viewModel: AppViewModel,
    paddingValues: PaddingValues
) {
    var selectedTab by remember { mutableStateOf("movie") }
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val tvShows = viewModel.tvShows.collectAsLazyPagingItems()



    Column(modifier = Modifier.fillMaxSize().background(LightBackgroundColor).padding(paddingValues)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { selectedTab = "movie" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "movie") LightPrimaryColor else LightSecondaryColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "Movies",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { selectedTab = "tv" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "tv") LightPrimaryColor else LightSecondaryColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = "TV Shows",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
    }

        if (movies.loadState.refresh is LoadState.Loading || tvShows.loadState.refresh is LoadState.Loading) {
            ShimmerEffect()
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
                items(if (selectedTab == "movie") movies.itemCount else tvShows.itemCount) { index ->
                    val movie = if (selectedTab == "movie") movies[index] else tvShows[index]
                    movie?.let {
                        MovieItem(movie, onClick = { navigateToMovieDetails(movie.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = movie.posterPath?:"https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg",
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(movie.title, fontWeight = FontWeight.Bold, color = LightPrimaryColor)
            Text("Year: ${movie.year}", style = MaterialTheme.typography.bodySmall, color = LightSecondaryColor)
        }
    }
}

@Composable
fun ShimmerEffect() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(LightBackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Please wait, it may take time.",
            color = LightPrimaryColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(10) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightSecondaryColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFBDBDBD))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFBDBDBD))
                    )
                }
            }
        }
    }
}



