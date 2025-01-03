package com.oxiion.campuscart_user.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppAlertBox(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = "Exit App",
    message: String = "Are you sure you want to exit?"
) {
    AlertDialog(
        containerColor = Color(0xFF29638A),
        title = {
            Box (modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center){
                Row {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color(0xFFBA1A1A)
                    )
                    Text(" ")
                    Text(text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        text = {
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color(0xFFFFDAD6),
                style = MaterialTheme.typography.bodyMedium)
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onConfirm, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ), modifier = Modifier.padding(vertical = 8.dp).size(width = 100.dp, height = 40.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Yes")
                }
                Button(
                    onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ), modifier = Modifier.padding(vertical = 8.dp).size(width = 100.dp, height = 40.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "No")
                }
            }
        },
    )
}