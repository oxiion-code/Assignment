package com.oxiion.campuscart_user.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButtonSmall
import com.oxiion.campuscart_user.viewmodels.AuthViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel
) {
    val productList by authViewModel.productList.collectAsState()
    val userData by authViewModel.userData.collectAsState()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding).background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Home Screen", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Welcome, ${userData.address?.fullName}!")
            for (i in productList.indices){
                if (productList[i].name==""){
                    Text(text ="it is empty")
                }
                Text("Product name ${productList[i].name}")
                Text("Product name ${productList[i].price}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}