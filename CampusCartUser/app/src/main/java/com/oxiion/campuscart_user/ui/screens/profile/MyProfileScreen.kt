package com.oxiion.campuscart_user.ui.screens.profile

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart_user.R
import com.oxiion.campuscart_user.navigation.Screens
import com.oxiion.campuscart_user.ui.components.AppBottomBar
import com.oxiion.campuscart_user.ui.components.AppOutlinedTextBox
import com.oxiion.campuscart_user.ui.components.AppTopBar
import com.oxiion.campuscart_user.ui.components.LoadingDialogTransparent
import com.oxiion.campuscart_user.ui.theme.backgroundLight
import com.oxiion.campuscart_user.ui.theme.errorContainerLight

import com.oxiion.campuscart_user.ui.theme.errorContainerLightMediumContrast
import com.oxiion.campuscart_user.ui.theme.errorDarkHighContrast
import com.oxiion.campuscart_user.ui.theme.errorLightHighContrast
import com.oxiion.campuscart_user.ui.theme.primaryContainerDark
import com.oxiion.campuscart_user.ui.theme.primaryLight

import com.oxiion.campuscart_user.ui.theme.secondaryContainerLightHighContrast

import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.utils.SharedPreferencesManager
import com.oxiion.campuscart_user.viewmodels.AuthViewModel


@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    navigateToScreen: (route: String) -> Unit,
    changeAddressScreen: () -> Unit
) {
    val context = LocalContext.current
    val userData by authViewModel.userData.collectAsState()
    val logoutState by authViewModel.logOutState.collectAsState()

    val verifyCurrentEmailState by authViewModel.verifyEmailState.collectAsState()

    val changePasswordState by authViewModel.changePasswordState.collectAsState()
    val deleteAccountState by authViewModel.deleteAccountState.collectAsState()

    val hostelName = SharedPreferencesManager.getHostelNumber(context)
    val uid = SharedPreferencesManager.getUid(context)
    val isLoading = remember { mutableStateOf(false) }
    val isLoadingD = remember { mutableStateOf(false) }
    val isChangePasswordClicked = remember { mutableStateOf(false) }
    val isDeleteButtonClicked = remember { mutableStateOf(false) }
    val isLogoutButtonClicked = remember { mutableStateOf(false) }
    val verifyCurrentEmailClicked = remember { mutableStateOf(false) }
    val isVerificationEmailSent = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (uid != null) {
            authViewModel.fetchUserData(uid)
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            hostelName?.let {
                AppTopBar(
                    title = "Profile",
                    isHomeScreen = false,
                    hostelName = it,
                    onBackClick = onBackClick
                )
            }
        },
        bottomBar = {
            AppBottomBar(
                currentScreen = Screens.Profile.ProfileScreen.route,
                onNavigate = { route ->
                    navigateToScreen(route)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            WalletCard(userData)
            AboutMeCard(userData, changeAddressScreen = changeAddressScreen)

            //To Change Password

            Text(
                text = "Change Password!",
                fontWeight = FontWeight(600),
                textDecoration = TextDecoration.Underline,
                color = primaryLight,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, bottom = 8.dp)
                    .clickable {
                        isChangePasswordClicked.value = true
                    },
                style = MaterialTheme.typography.titleMedium,
            )

            if (isChangePasswordClicked.value) {
                ChangeCredentialBox(
                    title = "Change Password",
                    label1 = "Current Password",
                    placeholder1 = "Enter current password",
                    label2 = "New Password",
                    placeholder2 = "Enter new password",
                    onConfirmation = { oldPassword, newPassword ->
                        authViewModel.changePassword(oldPassword, newPassword)
                    },
                    changePassword = isChangePasswordClicked
                )
                when (changePasswordState) {
                    is DataState.Error -> {
                        isLoading.value = false
                        isChangePasswordClicked.value = false
                        Toast.makeText(
                            context,
                            (changePasswordState as DataState.Error).message,
                            Toast.LENGTH_SHORT
                        ).show()
                        authViewModel.resetChangeCredentialState()
                    }

                    DataState.Idle -> {

                    }

                    DataState.Loading -> {
                        isLoading.value = true
                    }

                    DataState.Success -> {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            "Password changed",
                            Toast.LENGTH_SHORT
                        ).show()
                        isChangePasswordClicked.value = false
                        authViewModel.resetChangeCredentialState()
                    }
                }
            }


            Text(
                text = "Verify current email!",
                fontWeight = FontWeight(600),
                textDecoration = TextDecoration.Underline,
                color = primaryLight,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, bottom = 8.dp)
                    .clickable {
                        verifyCurrentEmailClicked.value = true
                        authViewModel.verifyEmail()
                    },
                style = MaterialTheme.typography.titleMedium,
            )
            if (verifyCurrentEmailClicked.value) {
                when (verifyCurrentEmailState) {
                    is DataState.Error -> {
                        isLoading.value=false
                        Toast.makeText(
                            context,
                            (verifyCurrentEmailState as DataState.Error).message,
                            Toast.LENGTH_SHORT
                        ).show()
                        verifyCurrentEmailClicked.value = false
                        authViewModel.resetChangeCredentialState()
                    }

                    DataState.Idle -> {

                    }

                    DataState.Loading -> {
                        isLoading.value = true
                    }

                    DataState.Success -> {
                        isLoading.value=false
                        Toast.makeText(
                            context,
                            "Email sent successfully. Please verify it.",
                            Toast.LENGTH_SHORT
                        ).show()
                        verifyCurrentEmailClicked.value = false
                        authViewModel.resetChangeCredentialState()
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = errorContainerLightMediumContrast,
                        contentColor = errorDarkHighContrast
                    ),
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        isDeleteButtonClicked.value = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete Account", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = secondaryContainerLightHighContrast,
                        contentColor = errorDarkHighContrast
                    ),
                    shape = RoundedCornerShape(4.dp), onClick = {
                        authViewModel.logOut()
                        isLogoutButtonClicked.value = true
                    }, modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Logout",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            if (isLoading.value) {
                LoadingDialogTransparent(isLoading)
            }


            if (isLogoutButtonClicked.value) {
                when (logoutState) {
                    is DataState.Error -> {
                        Toast.makeText(
                            context,
                            (logoutState as DataState.Error).message,
                            Toast.LENGTH_SHORT
                        ).show()
                        authViewModel.resetLogoutState()
                    }

                    DataState.Idle -> {

                    }

                    DataState.Loading -> {
                        isLoading.value = true
                    }

                    DataState.Success -> {
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        isChangePasswordClicked.value = false
                        isDeleteButtonClicked.value = false
                        authViewModel.resetLogoutState()
                        navigateToScreen(Screens.Auth.SignIn.route)
                    }
                }
            }
            if (isDeleteButtonClicked.value) {
                DeletionDialog(
                    isDeleteButtonClicked,
                    onConfirmation = { password ->
                        authViewModel.deleteAccount(password = password)
                    }
                )
                when (deleteAccountState) {
                    is DataState.Error -> {
                        isLoading.value = false
                        isDeleteButtonClicked.value = false
                        Toast.makeText(
                            context,
                            (deleteAccountState as DataState.Error).message,
                            Toast.LENGTH_SHORT
                        ).show()
                        authViewModel.resetChangeCredentialState()
                    }

                    DataState.Idle -> {

                    }

                    DataState.Loading -> {
                        isLoading.value = true
                    }

                    DataState.Success -> {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            "Account deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        isDeleteButtonClicked.value = false
                        authViewModel.resetChangeCredentialState()
                        navigateToScreen(Screens.Auth.SignIn.route)
                    }
                }
            }
        }
    }
}


