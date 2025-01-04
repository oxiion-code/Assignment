package com.oxiion.campuscart_user.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart_user.R
@Composable
fun AppOutlinedTextBox(
    givenValue: MutableState<String>,
    label: String,
    keyboardType: KeyboardType,
    isPassword: Boolean,
    isPasswordVisible: (MutableState<Boolean>)? = null,
) {
    OutlinedTextField(
        value = givenValue.value,
        onValueChange = { newValue ->
            givenValue.value = newValue
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType
        ),
        visualTransformation = if (isPassword && (isPasswordVisible?.value == false)) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        label = { Text(text = label, color = Color.White) },
        modifier = Modifier.size(300.dp, 65.dp),
        singleLine = true,
        trailingIcon = {
            if (isPassword && isPasswordVisible != null) {
                IconButton(onClick = {
                    isPasswordVisible.value = !isPasswordVisible.value
                }) {
                    Icon(
                        painter = if (isPasswordVisible.value) {
                            painterResource(id = R.drawable.baseline_visibility)
                        } else {
                            painterResource(id = R.drawable.baseline_visibility_off_24)
                        },
                        contentDescription = if (isPasswordVisible.value) {
                            "Hide Password"
                        } else {
                            "Show Password"
                        }
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.White,
            unfocusedContainerColor = Color(0xFF72787E),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.LightGray,
            focusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            errorBorderColor = Color.Red,
            errorContainerColor = Color.Red,
        ),
        shape = RoundedCornerShape(8.dp)
    )
}
