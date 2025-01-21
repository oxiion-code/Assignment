package com.oxiion.campuscart_user.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart_user.ui.theme.secondaryContainerLightHighContrast

@Composable
fun ChangeCredentialBox(
    title: String,
    label1: String,
    label2: String,
    placeholder1: String,
    placeholder2: String,
    changePassword: MutableState<Boolean>,
    onConfirmation: (String, String) -> Unit
) {
    if (changePassword.value) {
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { },
            containerColor = secondaryContainerLightHighContrast,
            icon = {
                Icon(Icons.Filled.Lock, contentDescription = "Lock", tint = Color(0xFFFFDAD6))
            },
            title = {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        shape = RoundedCornerShape(10.dp),
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = {
                            Text(
                                label1,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        placeholder = {
                            Text(
                                placeholder1,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Background remains white
                            focusedTextColor = Color(0xFF111827), // Dark gray text
                            focusedBorderColor = Color(0xFF3B82F6), // Light blue border
                            unfocusedBorderColor = Color(0xFFE5E7EB), // Light gray border
                            unfocusedTextColor = Color(0xFF9CA3AF), // Medium gray text
                            cursorColor = Color(0xFF3B82F6), // Cursor matches primary color
                            errorBorderColor = Color(0xFFEF4444) // Error border for validation
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        shape = RoundedCornerShape(10.dp),
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = {
                            Text(
                                label2,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        placeholder = {
                            Text(
                                placeholder2,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // Background remains white
                            focusedTextColor = Color(0xFF111827), // Dark gray text
                            focusedBorderColor = Color(0xFF3B82F6), // Light blue border
                            unfocusedBorderColor = Color.LightGray, // Light gray border
                            unfocusedTextColor = Color(0xFF9CA3AF), // Medium gray text
                            cursorColor = Color(0xFF3B82F6), // Cursor matches primary color
                            errorBorderColor = Color(0xFFEF4444) // Error border for validation
                        )
                    )
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                ) {
                    TextButton(
                        onClick = { changePassword.value = false }
                    ) {
                        Text("Cancel", color = Color(0xFFFFDAD6))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFDAD6),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            onConfirmation(currentPassword, newPassword)
                            Log.d(
                                "ChangePassword",
                                "Save clicked with current: $currentPassword, new: $newPassword"
                            )
                        }
                    )//
                    {
                        Text("okay")
                    }
                }
            }
        )
    }

}
