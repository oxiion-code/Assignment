package com.oxiion.campuscart.domain.screens.adminScreens

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oxiion.campuscart.common.AppExitDialog
import com.oxiion.campuscart.common.LoadingDialog
import com.oxiion.campuscart.common.TopCampusAppBar
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.utils.LoginState
import com.oxiion.campuscart.utils.SharedPreferencesManager
import com.oxiion.campuscart.utils.StateData

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AdminDashboard(
    viewModel: AuthViewModel,
    onLogoutClick:()->Unit,
    onManageUsersClick:()->Unit,
    onManageCamusMenClick:()->Unit,
    onManageProductsClick:()->Unit) {

    val isLoading= remember { mutableStateOf(false) }
    val isLoadingKey= remember { mutableStateOf(false) }
    val activity= LocalContext.current as Activity
    var showExitDialog by remember { mutableStateOf(false) }
    val logoutState by viewModel.signOutState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager= LocalClipboardManager.current

    val adminData by viewModel.adminData.collectAsStateWithLifecycle()
    val generateKeyState by viewModel.generateKeyState.collectAsState()
    val key by viewModel.key.collectAsState()
    BackHandler {
        showExitDialog=true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFD8C4A0),
        topBar = {
            TopCampusAppBar(topBarTitle = "Admin Dashboard") {}
        }
    ) { screenPadding ->
        // Add your dashboard content here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(screenPadding)
        ) {
            Text(
                text="Hello ${adminData?.name}",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(9.dp))
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                onClick = {
                    onManageCamusMenClick()
                },
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF5C4300)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), // Ensures Box fills both width and height
                    contentAlignment = Alignment.Center // Centers content both vertically and horizontally
                ) {
                    Text(
                        text = "Manage CampusMen",
                        fontSize = 20.sp,
                        color = Color(0xFFD8C4A0),
                        style = MaterialTheme.typography.titleMedium
                    )
                } // Box
            } // Card
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                onClick = {
                    onManageProductsClick()
                },
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF5C4300)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), // Ensures Box fills both width and height
                    contentAlignment = Alignment.Center // Centers content both vertically and horizontally
                ) {
                    Text(
                        text = "Manage Products",
                        fontSize = 20.sp,
                        color = Color(0xFFD8C4A0),
                        style = MaterialTheme.typography.titleMedium
                    )
                } // Box
            } // Card

            Card( //secret id
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                onClick = {
                    if (adminData?.securityCode.isNullOrEmpty()){
                        viewModel.generateKeyId("Admin key", adminData!!.email)
                    }else{
                        clipboardManager.setText(AnnotatedString(text=adminData!!.securityCode ?: ""))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(
                        width = 250.dp,
                        height = 60.dp
                    )
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), // Ensures Box fills both width and height
                    contentAlignment = Alignment.Center // Centers content both vertically and horizontally
                ) {
                    Text(
                        text = if(adminData?.securityCode.isNullOrEmpty())"Generate secret id" else adminData?.securityCode.toString(),
                        fontSize = 20.sp,
                        color = Color(0xFFD8C4A0),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } // Box
            } // Card

            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 16.dp
                    ),
                    onClick = {
                        viewModel.logout()
                        SharedPreferencesManager.saveLogOutState(context, true)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF410002)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(140.dp, 50.dp)
                ) {
                    Text(
                        text = "Logout",
                        fontSize = 18.sp,
                        color = Color(0xFFD0C5B4))
                }
            }
            if (showExitDialog) {
                AppExitDialog(
                    onDismiss = { showExitDialog = false },
                    onConfirm = {activity.finish()}
                )
                // Add your exit dialog here
            }
            if(adminData?.securityCode.isNullOrEmpty()){
                when(generateKeyState){
                    is StateData.Error -> {
                        isLoadingKey.value=false
                        Toast.makeText(context, (generateKeyState as StateData.Error).message, Toast.LENGTH_SHORT).show()
                        viewModel.resetKeyState()
                    }
                    StateData.Idle -> {

                    }
                    StateData.Loading -> {
                        isLoadingKey.value=true
                        LoadingDialog(isLoadingKey)
                    }
                    StateData.Success -> {
                        viewModel.refreshAdminData()
                        isLoading.value=false
                        Toast.makeText(context, "Key generated", Toast.LENGTH_SHORT).show()
                        viewModel.resetKeyState()
                    }
                }
            }
            when (logoutState) {
                is LoginState.Error -> {
                    isLoading.value = false
                    Log.i("Logout error", "Error: ${(logoutState as LoginState.Error).message}")
                }
                LoginState.Idle -> { /* No-op */ }
                LoginState.Loading -> {
                    isLoading.value = true
                    LoadingDialog(isLoading)
                }
                LoginState.Success -> {

                    isLoading.value = false
                    Log.i("isLoading", "Success")
                    onLogoutClick() // Trigger navigation here
                }
            }
//when block
        }
    }
}