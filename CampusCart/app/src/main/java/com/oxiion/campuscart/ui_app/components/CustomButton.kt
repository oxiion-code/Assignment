package com.oxiion.campuscart.ui_app.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(text: String, onButtonClick: () -> Unit) {
    Button(
        onClick = {
            onButtonClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD8C4A0),
            contentColor = Color(0xFF402D00)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier =  Modifier.size(width = 300.dp, height = 46.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 16.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            )
    }
}