package com.oxiion.campuscart_user.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialogSmall(isLoading: MutableState<Boolean>) {
    if (isLoading.value) {
        Dialog(
            onDismissRequest = {}
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth().height(80.dp)
                    .background(Color(0xFF29638A)),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF29638A)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Loading...",
                            color = Color.White,
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingDialogFullScreen(isLoading: MutableState<Boolean>) {
    if (isLoading.value) {
        Dialog(
            onDismissRequest = {}
        ) {
            Surface(
                modifier = Modifier
                    . fillMaxWidth().height(60.dp)
                    .background(Color(0xFFFFFFFF)),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFFFFF)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF29638A),
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Loading...",
                            color = Color(0xFF29638A),
                            fontSize = 28.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun LoadingDialogTransParent(isLoading: MutableState<Boolean>) {
    if (isLoading.value) {
        Dialog(
            onDismissRequest = {}
        ) {
            Surface(
                modifier = Modifier
                    . fillMaxWidth().height(60.dp)
                    .background(Color.Transparent),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFFFFF)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF29638A),
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Loading...",
                            color = Color(0xFF29638A),
                            fontSize = 28.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}