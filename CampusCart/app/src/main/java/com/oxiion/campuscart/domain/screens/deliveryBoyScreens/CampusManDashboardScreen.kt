package com.oxiion.campuscart.domain.screens.deliveryBoyScreens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oxiion.campuscart.data.models.roles.User
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CampusManDashboardScreen(user: User){
    Scaffold (
        modifier = Modifier.fillMaxSize()
    ){padding->
        Column (modifier = Modifier.fillMaxSize().padding(padding)){
            Text("CampusMan Dashboard")
        }
    }
}