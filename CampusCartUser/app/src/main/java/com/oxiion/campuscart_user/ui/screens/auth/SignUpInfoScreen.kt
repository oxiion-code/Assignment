package com.oxiion.campuscart_user.ui.screens.auth

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButton
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButtonSmall
import com.oxiion.campuscart_user.ui.components.AppOutlinedTextBox
import com.oxiion.campuscart_user.ui.components.ShowTextSelectionDialog
import com.oxiion.campuscart_user.viewmodels.AuthViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SignUpInfoScreen(
    paddingValues: PaddingValues,
    onSignUpSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    // Local states
    val name = remember { mutableStateOf("") }
    val mobileNumber = remember { mutableStateOf("") }
    val selectedCollege = remember { mutableStateOf("") }
    val selectedHostel = remember { mutableStateOf("") }
    val showTextSelectionDialogCollege = remember { mutableStateOf(false) }
    val showTextSelectionDialogHostel = remember { mutableStateOf(false) }

    val collegeList by authViewModel.collegeList.collectAsState()
    val hostelList by authViewModel.hostelList.collectAsState()

    // State flows collected from ViewModel

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFF50606F))
            .imePadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Wait a second!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppOutlinedTextBox(
                givenValue = name,
                label = "Name",
                keyboardType = KeyboardType.Text,
                isPassword = false,
                isPasswordVisible = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppOutlinedTextBox(
                givenValue = mobileNumber,
                label = "Mobile Number",
                keyboardType = KeyboardType.Phone,
                isPassword = false,
                isPasswordVisible = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppCustomWhiteButtonSmall(
                onClick = {
                    authViewModel.getCollegeList()
                    showTextSelectionDialogCollege.value = true
                },
                text = selectedCollege.value.ifEmpty { "Select college" }
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppCustomWhiteButtonSmall(
                onClick = {
                    if (selectedCollege.value.isNotEmpty()) {
                        authViewModel.getHostelListIfNeeded(collegeName = selectedCollege.value)
                        showTextSelectionDialogHostel.value = true
                    } else {
                        Toast.makeText(context, "Please select a college first", Toast.LENGTH_SHORT).show()
                    }
                },
                text = selectedHostel.value.ifEmpty { "Select hostel " }
            )
        }
        AppCustomWhiteButton(
            onClick = {
                Toast.makeText(context, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                onSignUpSuccess()
            },
            text = "Create Account",
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (showTextSelectionDialogCollege.value) {
            ShowTextSelectionDialog(
                showDialog = showTextSelectionDialogCollege,
                selectedCollege = selectedCollege,
                collageList = collegeList
            )
        }
        if (showTextSelectionDialogHostel.value) {
            ShowTextSelectionDialog(
                showDialog = showTextSelectionDialogHostel,
                selectedCollege = selectedHostel,
                collageList = hostelList
            )
        }
    }
}
