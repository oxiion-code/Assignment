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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart.R

@Composable
fun CustomBlackGreenTextBox(text:MutableState<String>,placeholder:String){
    OutlinedTextField(value =text.value ,
        modifier = Modifier.size(width = 300.dp, height = 56.dp),
        onValueChange = { text.value = it },
        placeholder = {
            Text(
                text = placeholder, color = colorResource(R.color.white)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFFFFFFFF),
            focusedContainerColor = Color(0xFF082008),
            unfocusedContainerColor = Color(0xFF000000),
            focusedBorderColor = Color(0xFFEAC16C),
            unfocusedBorderColor = Color(0xFFFFFFFF),
            unfocusedTextColor = Color(0xFFCCEBC3)

        )
    )
    Spacer(modifier = Modifier.height(16.dp))
}