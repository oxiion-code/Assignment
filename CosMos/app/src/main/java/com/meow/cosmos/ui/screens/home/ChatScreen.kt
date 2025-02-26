package com.meow.cosmos.ui.screens.home

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.meow.cosmos.R
import com.meow.cosmos.database.local.ChatEntity
import com.meow.cosmos.viewModels.ChatViewModel

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    navigateToCardsScreen:()->Unit) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/raw/background_video")
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    var message by remember { mutableStateOf("") }
    val chatMessages by chatViewModel.chatList.collectAsState() // Get messages from ViewModel
    var isMessageSent by remember { mutableStateOf(false) } // Flag to check if the user sent at least one message
    var isSending by remember { mutableStateOf(false) } // Flag to disable send button while processing
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    BackHandler {
        (context as? Activity)?.finish()
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding().navigationBarsPadding(),
        bottomBar = {
            ChatInputField(
                message = message,
                onMessageChange = { message = it },
                onSendClick = {
                    if (message.isNotBlank()) {
                        isSending = true
                        chatViewModel.sendMessage(message, isFromApp = false) // Save to Room DB
                        message = "" // Clear input field
                        isMessageSent = true // Enable the clickable image
                        keyboardController?.hide()
                    }
                },
                isMessageSent=isMessageSent
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background Video
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // COSMOS Text
                    Text(
                        text = "COSMOS",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .shadow(8.dp, shape = RoundedCornerShape(10.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF8A2BE2), Color(0xFF483D8B), Color.Transparent),
                                    radius = 120f
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Clear Icon Button
                    IconButton(
                        onClick = { chatViewModel.clearChatHistory() } // Call the function to clear chat
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_restore_page), // Default delete icon
                            contentDescription = "Clear Chat",
                            tint = Color(0xFFFF6347), // Tomato Red (works well on black background)
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    state = listState
                ) {
                    item {
                        ChatItem(
                            ChatEntity(
                                senderIsApp = true,
                                message = "Tell me, are you looking for insights on love, career, health, or something else?"
                            )
                        )
                    }
                    items(chatMessages) { chat ->
                        ChatItem(chat = chat)
                    }
                    if (isMessageSent) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .width(250.dp) // Increased width
                                        .clickable {
                                            navigateToCardsScreen()
                                            isMessageSent = false
                                                   }, // Click effect on the entire card
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 8.dp
                                    ),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp), // Padding around the content
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.navigation_image),
                                            contentDescription = "Click for insights",
                                            modifier = Modifier
                                                .fillMaxWidth() // Make the image cover the width of the card
                                                .height(160.dp) // Slightly increased height for better proportion
                                                .clip(RoundedCornerShape(12.dp)), // Soft rounded corners for image
                                            contentScale = ContentScale.Crop // Ensures the image fills properly
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Tap the card and let the magic unfold...",
                                            fontSize = 14.sp,
                                            color = Color.Black,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (chatMessages.isNotEmpty()){
                    LaunchedEffect(chatMessages) {
                        // Scroll to the last item in the LazyColumn when messages are updated
                        listState.animateScrollToItem(chatMessages.size - 1)
                    }
                }
            }
        }
    }
}



@Composable
fun ChatItem(chat: ChatEntity) {
    val isAppMessage = chat.senderIsApp
    val aiResponse = chat.aiResponse.isNotBlank()

    // Separate Glow Colors
    val glowColors = if (isAppMessage) {
        listOf(Color.White.copy(alpha = 0.9f), Color(0xFF8A2BE2).copy(alpha = 0.6f))
    } else {
        listOf(Color.Cyan.copy(alpha = 0.9f), Color.Blue.copy(alpha = 0.6f))
    }

    var showAiResponseDialog by remember { mutableStateOf(false) }

    // Show the dialog when "AI" text is clicked
    if (showAiResponseDialog && aiResponse) {
        AlertDialog(
            onDismissRequest = { showAiResponseDialog = false },
            title = {
                Text(text = "AI Response", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Text(text = chat.aiResponse, color = Color.White)
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(4.dp),
                    onClick = { showAiResponseDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D619A)),
                    content = { Text("OK", color = Color.White) }
                )
            },
            containerColor = Color(0xFF2C3E50), // Dark background color for the dialog
            titleContentColor = Color.White,
            shape = MaterialTheme.shapes.medium,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = if (isAppMessage) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.Bottom // Ensures icons stay aligned
    ) {
        if (isAppMessage) {
            GlowingIcon(
                imageRes = R.drawable.app_chat_person,
                glowColors = glowColors
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Chat Message Box
        Box(
            modifier = Modifier
                .weight(1f) // Allows text to expand without affecting icons
                .background(
                    color = if (isAppMessage) Color(0x80FFFFFF) else Color(0x8050C878),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(14.dp)
        ) {
            Column {
                // Chat Message Text
                Text(
                    text = chat.message,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                // If AI response exists, show the "AI" label under the message
                if (aiResponse) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "AI Response",
                        modifier = Modifier
                            .clickable { showAiResponseDialog = true }
                            .padding(8.dp)
                            .background(Color(0xFF7D619A), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (!isAppMessage) {
            Spacer(modifier = Modifier.width(8.dp))
            GlowingIcon(
                imageRes = R.drawable.user_image,
                glowColors = glowColors
            )
        }
    }
}



@Composable
fun GlowingIcon(imageRes: Int, glowColors: List<Color>) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(60.dp)
            .background(Color.Transparent)
    ) {
        // Outer Glow Effect
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = glowColors,
                    center = center,
                    radius = size.width / 2
                ),
                radius = size.width / 2
            )
        }

        // Profile Image (Crisp & Clear)
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Profile Icon",
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputField(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isMessageSent: Boolean // Flag to check if the user sent at least one message
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(Color.Transparent)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF62B6CB), Color(0xFF1B4965)),
                        center = center,
                        radius = size.width / 2
                    )
                )
            }
            Image(
                painter = painterResource(id = R.drawable.user_image),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Message Input Field
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = { Text("Type a message...", color = Color(0xFFB0C4DE)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1E90FF),
                unfocusedBorderColor = Color(0xFF4682B4),
                focusedTextColor = Color.White,
                cursorColor = Color(0xFF87CEFA),
                containerColor = Color(0xFF101820)
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Send Button
        IconButton(
            onClick = onSendClick,
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF1E90FF), Color(0xFF00BFFF))
                    ),
                    shape = CircleShape
                )
                .size(50.dp),
            enabled = !isMessageSent && message.isNotBlank() // Disable if processing or empty
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_send_24),
                contentDescription = "Send",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}











