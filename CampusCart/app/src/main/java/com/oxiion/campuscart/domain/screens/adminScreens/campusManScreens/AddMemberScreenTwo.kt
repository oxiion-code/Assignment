package com.oxiion.campuscart.domain.screens.adminScreens.campusManScreens


import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart.common.LoadingDialog
import com.oxiion.campuscart.common.TopCampusAppBar
import com.oxiion.campuscart.data.models.productUtils.Address
import com.oxiion.campuscart.data.models.roles.CampusMan
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.domain.models.CampusManViewModel
import com.oxiion.campuscart.ui_app.components.CustomBlackGreenTextBox
import com.oxiion.campuscart.ui_app.components.CustomButton
import com.oxiion.campuscart.utils.StateData
@Composable
fun AddMemberScreenTwo(
    campusManViewModel: CampusManViewModel,
    onBackClick: () -> Unit,
    adminViewModel: AuthViewModel
) {
    Scaffold(
        topBar = {
            TopCampusAppBar(
                topBarTitle = "Add Campus Member",
                onBackClick = { onBackClick() }
            )
        }
    ) { innerPadding ->
        val context = LocalContext.current
        val clipboardManager = LocalClipboardManager.current

        val isLoading = remember { mutableStateOf(false) }
        val campusManData by campusManViewModel.campusManData.collectAsState()
        val uniqueCode by campusManViewModel.campusManId.collectAsState()
        val addMemberState by campusManViewModel.addCampusManState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD8C4A0))
                .padding(innerPadding)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(campusManData?.address?.fullName ?: "")

            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    CustomBlackGreenMemberBox(
                        text = remember { mutableStateOf(campusManData!!.address.email) },
                        placeholder = "Enter email address",
                        onValueChange = {
                            campusManViewModel.saveCampusManData(
                                campusManData!!.copy(
                                    address = campusManData!!.address.copy(email = it)
                                )
                            )
                        }
                    )
                }
                item {
                    CustomBlackGreenMemberBox(
                        text = remember { mutableStateOf(campusManData!!.address.rollNumber) },
                        placeholder = "Enter roll number",
                        onValueChange = {
                            campusManViewModel.saveCampusManData(
                                campusManData!!.copy(
                                    address = campusManData!!.address.copy(rollNumber = it)
                                )
                            )
                        }
                    )
                }
                item {
                    Card(
                        modifier = Modifier
                            .size(200.dp, 60.dp)
                            .clickable {
                                clipboardManager.setText(AnnotatedString(uniqueCode ?: ""))
                            }
                            .background(Color.Black),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uniqueCode ?: "Campusman ID",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight(600),
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                enabled = uniqueCode == null,
                onClick = { campusManViewModel.generateCampusManId(cause="CampusMan Id", email = campusManData!!.address.email) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF402D00),
                    contentColor = Color(0xFFD8C4A0)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(width = 300.dp, height = 46.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 16.dp)
            ) {
                Text(
                    text = "Generate ID",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            CustomButton("Add Member") {
                val data = campusManData ?: return@CustomButton Toast.makeText(
                    context,
                    "Data is incomplete",
                    Toast.LENGTH_LONG
                ).show()

                if (data.address.fullName.isNullOrEmpty() ||
                    data.address.hostelNumber.isNullOrEmpty() ||
                    data.address.roomNumber.isNullOrEmpty() ||
                    data.address.phoneNumber.isNullOrEmpty() ||
                    data.address.email.isNullOrEmpty() ||
                    data.address.rollNumber.isNullOrEmpty() ||
                    data.imageUrl == "null" ||
                    uniqueCode == null) {
                    Toast.makeText(context, "Please fill all fields and upload an image", Toast.LENGTH_LONG).show()
                } else {
                    val updatedCampusManData = data.copy(
                        id = uniqueCode!!,
                        address = data.address.copy(
                            email = data.address.email,
                            rollNumber = data.address.rollNumber
                        )
                    )
                    campusManViewModel.addCampusManWithImage(
                        uri = Uri.parse(data.imageUrl),
                        campusMan = updatedCampusManData,
                        authViewModel = adminViewModel
                    )
                    campusManViewModel.resetUniqueId()
                }
            }

            when (addMemberState) {
                is StateData.Error -> {
                    isLoading.value=false
                    Toast.makeText(context, "Try again", Toast.LENGTH_LONG).show()
                    campusManViewModel.resetAddMemberState()
                }

                StateData.Idle -> {

                }

                StateData.Loading -> {
                    isLoading.value=true
                    LoadingDialog(isLoading)
                }

                StateData.Success -> {
                    isLoading.value=false
                    Toast.makeText(
                        context,
                        "Created campus member successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    campusManViewModel.resetAddMemberState()
                    campusManViewModel.resetCampusManData()
                }
            }
        }
    }
}
