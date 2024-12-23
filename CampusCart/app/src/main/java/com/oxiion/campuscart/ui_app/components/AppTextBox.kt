package com.oxiion.campuscart.ui_app.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppTextBox(text: MutableState<String>, placeholderName: String){
    OutlinedTextField(
        value = text.value,
        singleLine = true,
        modifier = Modifier.size(width = 300.dp, height = 56.dp),
        shape = RoundedCornerShape(10.dp),
        onValueChange = {text.value=it},
        placeholder = {
            Text(text = placeholderName,
                color = Color.DarkGray)
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = Color.Gray,
            focusedTextColor = Color.Black,
            focusedBorderColor = Color(0xFFEAC16C),
            unfocusedBorderColor = Color(0xFFFFFFFF),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF1E7D9)
        )
    )
    Spacer(modifier = Modifier.height(16.dp))
}