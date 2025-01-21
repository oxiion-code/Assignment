package com.oxiion.campuscart_user.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart_user.ui.components.AppOutlinedTextBox
import com.oxiion.campuscart_user.ui.theme.errorContainerLight
import com.oxiion.campuscart_user.ui.theme.errorContainerLightMediumContrast
import com.oxiion.campuscart_user.ui.theme.errorLightHighContrast
import com.oxiion.campuscart_user.ui.theme.tertiaryDark
import com.oxiion.campuscart_user.ui.theme.tertiaryDarkHighContrast


@Composable
fun DeletionDialog(
    isDeletionButtonClicked: MutableState<Boolean>,
    onConfirmation: (String) -> Unit
) {
    val password = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (isDeletionButtonClicked.value) {
        AlertDialog(
            containerColor = errorContainerLightMediumContrast,
            onDismissRequest = {
                isDeletionButtonClicked.value = false
            },
            title = {
                Text(
                    text = "Confirm Account Deletion",
                    style = MaterialTheme.typography.titleMedium,
                    color = tertiaryDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Please enter your password to confirm deletion:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tertiaryDarkHighContrast
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppOutlinedTextBox(
                        givenValue = password,
                        label = "Password",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible
                    )
                }
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = errorContainerLight,
                        contentColor = errorLightHighContrast
                    ),
                    onClick = {
                        if (password.value.isNotBlank()) {
                            onConfirmation(password.value)
                        } else {
                            Toast.makeText(context, "Password can not be blank", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                ) {
                    Text("Confirm Deletion")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isDeletionButtonClicked.value = false },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = errorContainerLight,
                        contentColor = errorLightHighContrast
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
