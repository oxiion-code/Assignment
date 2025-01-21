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
import com.oxiion.campuscart_user.data.model.Address
import com.oxiion.campuscart_user.data.model.User
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButton
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButtonSmall
import com.oxiion.campuscart_user.ui.components.AppOutlinedTextBox
import com.oxiion.campuscart_user.ui.components.LoadingDialogFullScreen
import com.oxiion.campuscart_user.ui.components.LoadingDialogSmall
import com.oxiion.campuscart_user.ui.components.ShowTextSelectionDialog
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.utils.DataStateAuth
import com.oxiion.campuscart_user.viewmodels.AuthViewModel
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
    val isSignUpClick= remember { mutableStateOf(false) }
    val isLoading= remember { mutableStateOf(false) }

    // Observing state
    val hostelListState by authViewModel.getHostelListState.collectAsState()
    val collegeList by authViewModel.collegeList.collectAsState()
    val hostelList by authViewModel.hostelList.collectAsState()
    val userData by authViewModel.userData.collectAsState()
    val signUpState by authViewModel.signUpState.collectAsState()

    fun validateInputs(): Boolean {
        if (name.value.isBlank()) {
            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (mobileNumber.value.isBlank()) {
            Toast.makeText(context, "Mobile number cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedCollege.value.isBlank()) {
            Toast.makeText(context, "Please select a college", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedHostel.value.isBlank()) {
            Toast.makeText(context, "Please select a hostel", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

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
                    showTextSelectionDialogCollege.value = true
                },
                text = selectedCollege.value.ifEmpty { "Select college" }
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppCustomWhiteButtonSmall(
                onClick = {
                    if (selectedCollege.value.isNotEmpty()) {
                        authViewModel.getHostelListIfNeeded(selectedCollege.value)
                        showTextSelectionDialogHostel.value = true
                    } else {
                        Toast.makeText(context, "Please select a college first", Toast.LENGTH_SHORT).show()
                    }
                },
                text = selectedHostel.value.ifEmpty { "Select hostel" }
            )
        }
        AppCustomWhiteButton(
            onClick = {
                if (validateInputs()) {
                    val password=userData.college
                    val user= User(
                        address = userData.address?.let {
                            Address(
                                email = it.email,
                                fullName = name.value,
                                phoneNumber = mobileNumber.value,
                                hostelNumber = selectedHostel.value
                            )
                        },
                        college = selectedCollege.value,
                        walletMoney = 0.0
                    )
                    isSignUpClick.value=true
                    authViewModel.signUp(user = user,password= password)
                }
            },
            text = "Create Account",
        )
        Spacer(modifier = Modifier.height(16.dp))

        // College Selection Dialog
        if (showTextSelectionDialogCollege.value) {
            ShowTextSelectionDialog(
                showDialog = showTextSelectionDialogCollege,
                selectedCollege = selectedCollege,
                collageList = collegeList,
                onSelectionChange = { newCollege ->
                    selectedHostel.value = "" // Clear selected hostel
                    authViewModel.clearHostelList() // Clear existing hostel list
                    authViewModel.getHostelListIfNeeded(newCollege) // Fetch new hostel list
                }
            )
        }

        // Hostel Selection Dialog
        if (showTextSelectionDialogHostel.value) {
            ShowTextSelectionDialog(
                showDialog = showTextSelectionDialogHostel,
                selectedCollege = selectedHostel,
                collageList = hostelList,
                onSelectionChange = {}
            )
        }

        // Handle hostel list state
        when (hostelListState) {
            is DataState.Error -> {
                Toast.makeText(context, "Unable to fetch hostels", Toast.LENGTH_SHORT).show()
            }
            DataState.Idle -> {

            }
            DataState.Loading -> {
                LoadingDialogSmall(remember { mutableStateOf(true) })
            }
            is DataState.Success -> {}
        }
        if (isSignUpClick.value){
           when(signUpState){
               is DataStateAuth.Error -> {
                   isLoading.value=false
                   Toast.makeText(context, (signUpState as DataStateAuth.Error).message, Toast.LENGTH_SHORT).show()
                   isSignUpClick.value=false
               }
               DataStateAuth.Idle -> {

               }
               DataStateAuth.Loading -> {
                   isLoading.value=true
                   LoadingDialogFullScreen(isLoading)
               }
               is DataStateAuth.Success -> {
                   isLoading.value=false
                   onSignUpSuccess()
                   isSignUpClick.value=false
               }
           }
        }
    }
}
