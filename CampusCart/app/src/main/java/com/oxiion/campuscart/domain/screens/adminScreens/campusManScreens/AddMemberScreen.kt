package com.oxiion.campuscart.domain.screens.adminScreens.campusManScreens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart.R
import com.oxiion.campuscart.common.TopCampusAppBar
import com.oxiion.campuscart.data.models.productUtils.Address
import com.oxiion.campuscart.data.models.roles.CampusMan
import com.oxiion.campuscart.domain.models.CampusManViewModel
import com.oxiion.campuscart.ui_app.components.CustomBlackGreenTextBox
import com.oxiion.campuscart.ui_app.components.CustomButton


@Composable
fun CustomBlackGreenMemberBox(text: MutableState<String>, placeholder:String,onValueChange:(String)->Unit){

    OutlinedTextField(value =text.value ,
        modifier = Modifier.size(width = 300.dp, height = 56.dp),
        onValueChange = {
            text.value=it
            onValueChange(it) },
        placeholder = {
            Text(
                text = placeholder, color = colorResource(R.color.white)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFFFFFFFF),
            focusedContainerColor = Color(0xFF082008),
            unfocusedContainerColor = Color(0xFF000000),
            focusedBorderColor = Color(0xFFEAC16C),
            unfocusedBorderColor = Color(0xFFFFFFFF),
            unfocusedTextColor = Color(0xFFCCEBC3)

        )
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun AddMemberScreenOne(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    campusManViewModel: CampusManViewModel
) {
    val campusManData by campusManViewModel.campusManData.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(Uri.parse(campusManData?.imageUrl ?:"" )) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri=uri
            campusManViewModel.saveCampusManData(
                campusManData!!.copy(imageUrl = uri.toString())
            )
        }
    }
    Scaffold(topBar = {
        TopCampusAppBar(topBarTitle = "Add Campus Member", onBackClick = {
            onBackClick()
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD8C4A0))
                .padding(innerPadding)
                .imePadding(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Image(
                        painter = if (imageUri != null) rememberAsyncImagePainter(imageUri)
                        else painterResource(R.drawable.default_image),
                        contentDescription ="campusman Image",
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.Gray, RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomButton("Upload Image") {
                        launcher.launch("image/*")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                   CustomBlackGreenMemberBox(
                       text= remember { mutableStateOf(campusManData!!.address.fullName) },
                       placeholder = "Enter name",
                       onValueChange = {
                           campusManViewModel.saveCampusManData(
                               campusManData!!.copy(address = campusManData!!.address.copy(fullName = it))
                           )
                       }
                   )
                }
                item {
                    CustomBlackGreenMemberBox(
                        text = remember { mutableStateOf(campusManData!!.address.hostelNumber) },
                        placeholder = "Enter hostel name",
                        onValueChange = {
                            campusManViewModel.saveCampusManData(
                               campusManData!!.copy(address = campusManData!!.address.copy(hostelNumber = it))
                           )
                        }
                    )
                }
                item {
                    CustomBlackGreenMemberBox(
                        text = remember { mutableStateOf(campusManData!!.address.roomNumber) },
                        placeholder = "Enter room number",
                        onValueChange = {
                            campusManViewModel.saveCampusManData(
                                campusManData!!.copy(address = campusManData!!.address.copy(roomNumber = it))
                            )
                        }
                    )
                }
                item {
                    CustomBlackGreenMemberBox(
                        text = remember { mutableStateOf(campusManData!!.address.phoneNumber) },
                        placeholder = "Enter phone number",
                        onValueChange = {
                            campusManViewModel.saveCampusManData(
                                campusManData!!.copy(address = campusManData!!.address.copy(phoneNumber = it))
                            )
                        }
                    )
                }
            }
            CustomButton("Next") {
                onNextClick()
            }
        }
    }
}