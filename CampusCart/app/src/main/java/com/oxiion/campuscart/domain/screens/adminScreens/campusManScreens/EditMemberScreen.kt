package com.oxiion.campuscart.domain.screens.adminScreens.campusManScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.oxiion.campuscart.common.TopCampusAppBar

@Composable
fun EditMemberScreen(onBackClick:()->Unit){
    Scaffold(
        topBar = {
            TopCampusAppBar(
                topBarTitle = "Edit Member",
                onBackClick = {
                    onBackClick()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize().background(Color(0xFFD8C4A0))
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        }
    }
}
