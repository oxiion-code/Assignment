package com.oxiion.campuscart_user.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart_user.ui.components.AppCustomBlueButton
import com.oxiion.campuscart_user.ui.components.AppTopBar
import com.oxiion.campuscart_user.ui.components.LoadingDialogTransparent
import com.oxiion.campuscart_user.ui.components.ShowTextSelectionDialog
import com.oxiion.campuscart_user.ui.theme.primaryContainerLight
import com.oxiion.campuscart_user.ui.theme.primaryLight
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.utils.SharedPreferencesManager
import com.oxiion.campuscart_user.viewmodels.AuthViewModel


@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF3B82F6) // Primary color (light blue)
                )
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White, // Background remains white
            focusedTextColor = Color(0xFF111827), // Dark gray text
            focusedBorderColor = Color(0xFF3B82F6), // Light blue border
            unfocusedBorderColor = Color(0xFFE5E7EB), // Light gray border
            unfocusedTextColor = Color(0xFF9CA3AF), // Medium gray text
            cursorColor = Color(0xFF3B82F6), // Cursor matches primary color
            errorBorderColor = Color(0xFFEF4444) // Error border for validation
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = Color(0xFF111827) // Primary text color (dark gray)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // Padding around the field
            .background(Color.White, RoundedCornerShape(12.dp))
    )
}


@Composable
fun ChangeDetailsScreen(
    authViewModel: AuthViewModel,
    navigationBack: () -> Unit
) {
    val context = LocalContext.current
    val college = SharedPreferencesManager.getCollege(context)

    if (college == null) {
        Text("Error: College information not found.")
        return
    }

    // Fetch initial data
    authViewModel.getHostelListIfNeeded(college)

    // Observing state
    val userData by authViewModel.userData.collectAsState()
    val hostelList by authViewModel.hostelList.collectAsState()
    val updateAddressState by authViewModel.updateDetailsState.collectAsState()

    // State variables for form fields
    var name by remember { mutableStateOf(userData.address?.fullName ?: "") }
    var phoneNumber by remember { mutableStateOf(userData.address?.phoneNumber ?: "") }
    val hostelNumber = remember { mutableStateOf(userData.address?.hostelNumber ?: "") }
    val showHostelDialog = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    val uid =SharedPreferencesManager.getUid(context)

    fun validateInputs(): Boolean {
        if (name.isBlank()) {
            Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (phoneNumber.isBlank()) {
            Toast.makeText(context, "Phone number cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (hostelNumber.value.isBlank()) {
            Toast.makeText(context, "Please select a hostel", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Change Details",
                isHomeScreen = false,
                hostelName = hostelNumber.value,
                onBackClick = navigationBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Name",
                value = name,
                onValueChange = { name = it }
            )
            CustomTextField(
                label = "Phone Number",
                value = phoneNumber,
                onValueChange = { phoneNumber = it }
            )
            AppCustomBlueButton(
                onClick = {
                    authViewModel.getHostelListIfNeeded(college) // Fetch hostel list
                    showHostelDialog.value = true
                },
                text = "Change Hostel - ${hostelNumber.value.ifEmpty { "Select" }}"
            )
            if (showHostelDialog.value) {
                ShowTextSelectionDialog(
                    showDialog = showHostelDialog,
                    selectedCollege = hostelNumber,
                    collageList = hostelList,
                    onSelectionChange = {
                        hostelNumber.value = it // Update hostel selection
                        showHostelDialog.value = false
                    }
                )
            }
            AppCustomBlueButton(
                onClick = {
                    if (validateInputs()) {
                        isLoading.value = true
                        authViewModel.updateDetails(
                            context = context,
                            updatedName = name,
                            updatedHostelNumber = hostelNumber.value,
                            updatedPhoneNumber = phoneNumber
                        )
                    }
                },
                text = "Save Settings"
            )
        }

        // Loading dialog
        if (isLoading.value) {
            LoadingDialogTransparent(isLoading)
        }
        when(updateAddressState){
            is DataState.Error -> {
                isLoading.value = false
                Toast.makeText(context, (updateAddressState as DataState.Error).message, Toast.LENGTH_SHORT).show()
                authViewModel.resetUpdateState()
            }
            DataState.Idle -> {

            }
            DataState.Loading -> {
                isLoading.value = true
            }
            DataState.Success -> {
                isLoading.value = false
                Toast.makeText(context, "Details updated successfully", Toast.LENGTH_SHORT).show()
                authViewModel.resetUpdateState()
                navigationBack()
            }
        }
    }
}


