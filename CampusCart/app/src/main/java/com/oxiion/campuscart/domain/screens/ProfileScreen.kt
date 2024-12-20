package com.oxiion.campuscart.domain.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart.common.ShowCollegeSelectionDialog
import com.oxiion.campuscart.common.ShowButtonDialog
import com.oxiion.campuscart.data.models.roles.Admin
@Composable
fun ProfileScreen(
    profileRole: MutableState<String>,
    collegeName: MutableState<String>,
    onNextClick: () -> Unit
) {

    val showDialog = remember { mutableStateOf(false) }
    val showCollageSelectionDialog = remember { mutableStateOf(false) }
    val colleges = listOf(
        "Indian Institute of Technology (IIT) Bombay",
        "Indian Institute of Technology (IIT) Delhi",
        "Indian Institute of Technology (IIT) Kanpur",
        "Indian Institute of Technology (IIT) Kharagpur",
    )
    val admin = Admin(
        name = "admin",
        securityCode = "",
        email = "",
        stockItems = listOf(),
        collageList = colleges,
        employeeList = listOf()
    )
    val options = listOf("Admin", "User", "CampusMan")
    ShowButtonDialog(showDialog = showDialog, options = options, optionSelected = profileRole)
    ShowCollegeSelectionDialog(
        showDialog = showCollageSelectionDialog,
        selectedCollege = collegeName,
        collageList = admin.collageList
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD8C4A0)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "CampusCart",
            color = Color(0xFF78590C),
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 43.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier
                .width(300.dp)
                .height(350.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .shadow(
                    elevation = 24.dp,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                ), // Clip the Card to have rounded corners // Set the shape for shadow and corners
            // Elevation for the shadow
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD8C4A0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://firebasestorage.googleapis.com/v0/b/campuscart-34351.firebasestorage.app/o/child_holding%20papers.jpg?alt=media&token=8bb85e45-2106-4afc-81ce-fbc08c69ba2b"
                ),
                contentDescription = "Image from URL",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize() // Fill the Card completely
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                showDialog.value = true
            },
            modifier = Modifier.width(300.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF78590C),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "I am ${profileRole.value}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 17.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                showCollageSelectionDialog.value = true
            },
            modifier = Modifier.width(300.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF78590C),
                contentColor = Color.White
            )
        ) {
            Text(
                text = collegeName.value,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 17.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onNextClick()
            },
            modifier = Modifier
                .width(180.dp)
                .align(Alignment.End)
                .padding(end = 52.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1F1B13),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Next",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 17.sp
            )
        }
    }
}
