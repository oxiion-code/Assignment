package com.oxiion.campuscart.domain.screens.userScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart.R
import com.oxiion.campuscart.ui_app.components.CustomButton


@Composable
fun UserLoginScreen(onNewUserClick:()->Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "CampusCart",
            color = Color(0xFFFFDEA1),
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 40.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text="Your Needs ! Our Priority",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            color =Color(0xFFFFDEA1) ,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Login", color = Color(0xFFF5E0BB), style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(46.dp))
        OutlinedTextField(value = email,
            modifier = Modifier.size(width = 300.dp, height = 56.dp),
            onValueChange = { email = it },
            placeholder = {
                Text(
                    text = "Email", color = colorResource(R.color.white)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFFFFFFFF),
                focusedContainerColor = Color(0xFF082008),
                unfocusedContainerColor = Color(0xFF000000),
                focusedBorderColor = Color(0xFFEAC16C),
                unfocusedBorderColor = Color(0xFFFFFFFF)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password,
            modifier = Modifier.size(width = 300.dp, height = 56.dp),
            onValueChange = { password = it },
            placeholder = {
                Text(
                    text = "Password", color = colorResource(R.color.white)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFFFFFFFF),
                focusedContainerColor = Color(0xFF082008),
                unfocusedContainerColor = Color(0xFF000000),
                focusedBorderColor = Color(0xFFEAC16C),
                unfocusedBorderColor = Color(0xFFFFFFFF)

            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        CustomButton("Login") {
            // Handle login logic
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Click here to create a new account.",
            color = Color(0xFFFDF2E5),
            modifier = Modifier.padding(2.dp).clickable {
                onNewUserClick()
            })
    }
}