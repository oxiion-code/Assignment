package com.oxiion.campuscart.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopCampusAppBar(topBarTitle: String, onBackClick: () -> Unit) {
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
//            IconButton(
//                onClick = {
//
//                },
//                content = {
//                    Icon(
//                        tint = Color(0xFF261900),
//                        modifier = Modifier.size(24.dp),
//                        painter = painterResource(R.drawable.add_member),
//                        contentDescription = "Search"
//                    )
//                }
//            )
        }

    )
}