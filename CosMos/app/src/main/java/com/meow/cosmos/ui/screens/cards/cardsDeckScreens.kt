package com.meow.cosmos.ui.screens.cards


import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import com.meow.cosmos.R
import com.meow.cosmos.models.TarotCard
import com.meow.cosmos.viewModels.ChatViewModel

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CardDeckScreen(
    chatViewModel: ChatViewModel,
    navigateToInputScreen: () -> Unit
) {
    val context = LocalContext.current
    val tarotCards = remember { mutableStateOf<List<TarotCard>>(emptyList()) }
    val selectedCards = remember { mutableStateOf<Set<TarotCard>>(emptySet()) }
    val showRevealedCards = remember { mutableStateOf(false) }
    val showAiResponseDialog = remember { mutableStateOf(false) }
    val aiResponse = remember { mutableStateOf("") }
    val sendMessageState by chatViewModel.sendMessageState.collectAsState()

    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        tarotCards.value = chatViewModel.loadTarotCards(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color.White,
                contentColor = Color.Blue,
                onClick = {
                    // Scroll to the next set of 4 items
                   Toast.makeText(context,"Scroll down", Toast.LENGTH_SHORT).show()
                },
                content = {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = Color.Blue)
                }
            )
        }

        ,
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (showRevealedCards.value) {
                                chatViewModel.sendMessage(
                                    message = selectedCards.value.toList()[0].description,
                                    aiResponse=aiResponse.value,
                                    isFromApp = true
                                )
                                navigateToInputScreen()
                                showRevealedCards.value = false
                                selectedCards.value = emptySet()
                            } else {
                                if (selectedCards.value.size == 3) showRevealedCards.value = true
                            }
                        },
                        enabled = selectedCards.value.size == 3 || showRevealedCards.value,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showRevealedCards.value) Color.Red else Color.Blue
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = if (showRevealedCards.value) "Go Back" else "Confirm Selection (${selectedCards.value.size}/3)",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    } //
                }
            }
        },
        content = { paddingValues ->
            if (showAiResponseDialog.value) {
                AlertDialog(
                    onDismissRequest = { showAiResponseDialog.value = false },
                    title = {
                        Text(
                            text = "AI Response",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        when (sendMessageState) {
                            is ChatViewModel.SendMessageState.Failure -> {
                                Text(text = "Failed to load", color = Color.Red)
                            }
                            ChatViewModel.SendMessageState.Idle -> {
                                Text(text = "Idle, waiting for action...", color = Color.Gray)
                            }
                            ChatViewModel.SendMessageState.Loading -> {
                                // Loading animation
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.LightGray, // Customize your loading color
                                        strokeWidth = 4.dp
                                    )
                                }
                                Text(text = "Loading...", color = Color.Gray)
                            }
                            is ChatViewModel.SendMessageState.Success -> {
                                aiResponse.value = (sendMessageState as ChatViewModel.SendMessageState.Success).response
                                Text(text = aiResponse.value, color = Color.Green)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            shape = RoundedCornerShape(4.dp),
                            onClick = { showAiResponseDialog.value = false },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7D619A), // Green button color
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text("OK")
                        }
                    },
                    containerColor = Color(0xFF2C3E50), // Dark background color for the dialog
                    titleContentColor = Color.White, // Text color for the content
                    shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                )
            }

            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.deck_backgrund),
                    contentDescription = "Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                if (!showRevealedCards.value) {
                    SelectionCardsScreen(
                        tarotCards = tarotCards.value,
                        selectedCards = selectedCards,
                        showRevealedCards = showRevealedCards,
                        gridState=gridState
                    )
                } else {
                    RevealedCardsScreen(
                        selectedCards = selectedCards.value.toList(),
                        showAiResponseDialog = showAiResponseDialog,
                        chatViewModel = chatViewModel,
                    )
                }
            }
        }
    )
}

@Composable
fun SelectionCardsScreen(
    tarotCards: List<TarotCard>,
    selectedCards: MutableState<Set<TarotCard>>,
    showRevealedCards: MutableState<Boolean>,
    gridState: LazyGridState
) {
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(2), // Two columns
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Horizontal padding for the entire grid
        verticalArrangement = Arrangement.spacedBy(16.dp) // Increased vertical spacing between cards
    ) {
        items(tarotCards) { card ->
            val isSelected = selectedCards.value.contains(card)
            // Combine front and back display logic based on showRevealedCards
            TarotCardItem(
                card = card,
                isSelected = isSelected,
                showFront = showRevealedCards.value,
                onClick = {
                    if (isSelected) {
                        selectedCards.value -= card
                    } else if (selectedCards.value.size < 3) {
                        selectedCards.value += card
                    }
                }
            )
        }
    }
}



@Composable
fun RevealedCardsScreen(
    chatViewModel: ChatViewModel,
    selectedCards: List<TarotCard>,
    showAiResponseDialog: MutableState<Boolean>,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Tarot Reading",
            fontSize = 28.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = {
                // Create a message with selected cards
                val cardNames = selectedCards.joinToString(", ") { it.name }
                val tarotMessage = "I have selected the following Tarot cards: $cardNames. Please interpret them in  easy words and in small paragraph"

                // Send message to Gemini API
                chatViewModel.sendMessage(
                    question = tarotMessage
                )

                // Show AI response dialog
                showAiResponseDialog.value = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(2.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Ask AI",
                fontSize = 18.sp,
                color = Color.White
            )
        }


        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(selectedCards) { card ->
                TarotCardFlipAnimation(card)
            }
        }
    }
}


@Composable
fun TarotCardItem(
    card: TarotCard,
    isSelected: Boolean,
    showFront: Boolean,
    onClick: () -> Unit
) {
    val fixedLink = fixDriveLink(card.link)
    val defaultImage = painterResource(id = R.drawable.default_card)

    Box(
        modifier = Modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) Color.Yellow else Color.Blue,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(8.dp), // Padding added around the Box for spacing between cards
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = if (showFront) rememberAsyncImagePainter(fixedLink) else defaultImage,
            contentDescription = if (showFront) card.name else "Tarot Card Back",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
    }
}


@Composable
fun TarotCardFlipAnimation(card: TarotCard) {
    var isFlipped by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isFlipped = true
    }

    val flipProgress by animateFloatAsState(
        targetValue = if (isFlipped) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .border(2.dp, Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black)
                .shadow(10.dp, shape = RoundedCornerShape(10.dp), ambientColor = Color.Blue)
                .clickable { isFlipped = !isFlipped },
            contentAlignment = Alignment.Center
        ) {
            if (flipProgress < 0.5f) {
                Image(
                    painter = painterResource(id = R.drawable.default_card),
                    contentDescription = "Tarot Card Back",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                SubcomposeAsyncImage(
                    model = fixDriveLink(card.link),
                    contentDescription = card.name,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize(),
                    loading = { ShimmerEffect() }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = card.name,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Text(
            text = card.description,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun ShimmerEffect() {
    val transition = rememberInfiniteTransition(label = "")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = alpha))
    )
}
fun fixDriveLink(link: String?): String? {
    return if (!link.isNullOrBlank() && link.contains("drive.google.com")) {
        val fileId = link.split("/d/")[1].split("/")[0]
        "https://drive.google.com/uc?export=view&id=$fileId"
    } else {
        null
    }
}