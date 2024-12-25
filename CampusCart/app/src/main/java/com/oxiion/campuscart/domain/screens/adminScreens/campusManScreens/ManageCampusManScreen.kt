package com.oxiion.campuscart.domain.screens.adminScreens.campusManScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart.R
import com.oxiion.campuscart.data.models.roles.CampusMan
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.domain.models.CampusManViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopCampusManageCampusMenBar(
    topBarTitle: String,
    onBackClick: () -> Unit,
    onAddMemberClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = topBarTitle,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color(0xFF261900)
            )
        },
        navigationIcon = {
            IconButton(
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFFFFDEA1),
                    contentColor = Color(0xFF261900)
                ),
                modifier = Modifier.size(30.dp),
                onClick = {
                    onBackClick()
                }) {
                Icon(
                    tint = Color(0xFF261900),
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back click"
                )
            }
        }//navigation
        , colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFEAC16C)
        ),
        actions = {
            IconButton(
                onClick = {
                    onAddMemberClick()
                },
                content = {
                    Icon(
                        tint = Color(0xFF261900),
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(R.drawable.add_product),
                        contentDescription = "Search"
                    )
                }
            )
        }

    )
}

@Composable
fun ManageCampusManScreen(
    adminViewModel: AuthViewModel,
    campusManViewModel: CampusManViewModel,
    onBackClick: () -> Unit,
    onAddMemberClick: () -> Unit,
    onEditMemberClick: (CampusMan) -> Unit,
) {
    val adminData = adminViewModel.adminData.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopCampusManageCampusMenBar(
                topBarTitle = "CampusMen",
                onBackClick = onBackClick,
                onAddMemberClick = onAddMemberClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFD8C4A0)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val count= adminData.value?.employeeList?.size
                if(count!=null){
                    items(count) { index ->
                        val campusMan = adminData.value?.employeeList?.get(index)
                        CampusManCard(
                            campusMan = campusMan!!,
                            onEditMemberClick = {
                                onEditMemberClick(campusMan)
                            }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun CampusManCard(
    campusMan: CampusMan,
    onEditMemberClick: (CampusMan) -> Unit
){
    Card(
        onClick = {
            onEditMemberClick(campusMan)
        },
        modifier = Modifier
           .padding(start = 16.dp, end = 16.dp, top = 16.dp).fillMaxWidth().height(70.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF78590C),
        )
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = campusMan.address.fullName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCCEBC3),
                fontSize = 20.sp
            )
            Text(
                text = campusMan.address.hostelNumber,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCCEBC3),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}