package com.oxiion.campuscart.domain.screens.adminScreens.campusManScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.oxiion.campuscart.common.TopCampusAppBar

@Composable
fun ManageStockItemsScreen(
    onBackClick:()->Unit,
    onAddStockItemClick:()->Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = { TopCampusManageCampusMenBar(
            topBarTitle = "Stock Items",
            onBackClick = onBackClick,
            onAddMemberClick = onAddStockItemClick
        ) }) { innerPadding->
        Column (modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFD8C4A0))){
        }
    }

}