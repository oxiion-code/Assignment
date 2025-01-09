package com.oxiion.campuscart_user.ui.components
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart_user.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    isHomeScreen: Boolean = false,
    hostelName: String = "Hostel Name",
    onBackClick:()->Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isHomeScreen) {
                    Icon(
                        painter = painterResource(id = R.drawable.logo_png_removebg_cropped), // Replace with your home icon
                        contentDescription = "Home Icon",
                        tint = Color(0xFF97CCF8),
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Space between icon and title
                }else{
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with your home icon
                            contentDescription = "Back Icon",
                            tint = Color(0xFF97CCF8),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp)) // Space between icon and title
                }
                Text(
                    text = title,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF181C20),
            titleContentColor = Color(0xFF97CCF8)
        ),
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.hostel), // Replace with your hostel icon
                    contentDescription = "Hostel Icon",
                    tint = Color(0xFF97CCF8),
                    modifier = Modifier
                        .size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and hostel name
                Text(
                    text = hostelName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF97CCF8)
                )
            }
        }
    )
}
