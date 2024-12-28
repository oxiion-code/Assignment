package com.oxiion.campuscart.domain.screens.adminScreens.campusManScreens

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart.common.AppExitDialog
import com.oxiion.campuscart.common.LoadingDialog
import com.oxiion.campuscart.common.TopCampusAppBar
import com.oxiion.campuscart.data.models.productUtils.Address
import com.oxiion.campuscart.data.models.roles.CampusMan
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.domain.models.CampusManViewModel
import com.oxiion.campuscart.domain.models.ProductViewModel
import com.oxiion.campuscart.ui_app.components.CustomBlackGreenTextBox
import com.oxiion.campuscart.ui_app.components.CustomButton
import com.oxiion.campuscart.utils.StateData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopCampusEditMemberAppBar(
    topBarTitle: String,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeliveryHistoryClick: () -> Unit,
    onStockItemsClick: () -> Unit,
    onLiveOrdersClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
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
            if (topBarTitle != "Admin Dashboard") {
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
            }
        }//navigation
        , colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFEAC16C)
        ),
        actions = {
            IconButton(
                onClick = {
                    //onDeleteClick()
                    expanded = !expanded
                },
                content = {
                    Icon(
                        tint = Color(0xFF261900),
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Search"
                    )
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF78590C))
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Stock Items",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCCEBC3)
                        )
                    },
                    onClick = {
                        expanded = false
                        // Handle Stock Items action here
                        onStockItemsClick()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Live orders", style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCCEBC3)
                        )
                    },
                    onClick = {
                        expanded = false
                        // Handle Delivery History action here
                        onLiveOrdersClick()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Past orders",  style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCCEBC3)
                        )
                    },
                    onClick = {
                        expanded = false
                        // Handle Delivery History action here
                        onDeliveryHistoryClick()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Delete Member", style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCCEBC3)
                        )
                    },
                    onClick = {
                        expanded = false
                        onDeleteClick()
                        // Handle Delete Member action here
                    }
                )
            }
        }//actions
    )

}


@Composable
fun EditMemberScreen(
    onLiveOrdersClick: () -> Unit,
    onBackClick: () -> Unit,
    onConfirmationDeletion: () -> Unit,
    onStockItemsClick: () -> Unit,
    onDeliveryHistoryClick: () -> Unit,
    campusman: CampusMan,
    authViewModel: AuthViewModel,
    campusManViewModel: CampusManViewModel
) {

    val campusmanUpdateState by campusManViewModel.updateCampusManState.collectAsState()
    val deleteCampusmanState by campusManViewModel.deleteCampusManState.collectAsState()
    val context = LocalContext.current

    val isLoading = remember { mutableStateOf(false) }
    val isLoadingDelete = remember { mutableStateOf(false) }

    val memberName = remember { mutableStateOf(campusman.address.fullName) }
    val phoneNumber = remember { mutableStateOf(campusman.address.phoneNumber) }
    val hostelNumber = remember { mutableStateOf(campusman.address.hostelNumber) }
    val id = remember { mutableStateOf(campusman.id) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showToast by remember { mutableStateOf(false) }
    val showDeleteConfirmDialog = remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it }
    }
    Scaffold(
        topBar = {
            TopCampusEditMemberAppBar(
                topBarTitle = "Edit Member",
                onBackClick = {
                    onBackClick()
                },
                onDeleteClick = {
                    showDeleteConfirmDialog.value = true
                },
                onDeliveryHistoryClick = {
                    onDeliveryHistoryClick()
                },
                onStockItemsClick = {
                    onStockItemsClick()
                },
                onLiveOrdersClick = {
                    onLiveOrdersClick()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFD8C4A0))
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showDeleteConfirmDialog.value) {
                AppExitDialog(onDismiss = {
                    showDeleteConfirmDialog.value = false
                }, onConfirm = {
                    campusManViewModel.deleteCampusman(campusman, authViewModel)
                },
                    title = "Delete Product",
                    message = "Do you want to delete this product?"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    Image(
                        painter = if (imageUri != null)
                            rememberAsyncImagePainter(imageUri)
                        else rememberAsyncImagePainter(campusman.imageUrl),
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.Gray, RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomButton("Change Image") {
                        launcher.launch("image/*")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(
                        text = "CampusMan Id: ${id.value}",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight(600)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    CustomBlackGreenTextBox(text = memberName, placeholder = "Edit member name")
                }
                item {
                    CustomBlackGreenTextBox(
                        text = phoneNumber,
                        placeholder = "Edit member phone number"
                    )
                }
                item {
                    CustomBlackGreenTextBox(
                        text = hostelNumber,
                        placeholder = "Edit hostel number"
                    )
                }
            }
            CustomButton("Save Changes") {
                if (memberName.value.isBlank() ||
                    phoneNumber.value.isBlank() ||
                    hostelNumber.value.isBlank()
                ) {
                    Toast.makeText(
                        context,
                        "Please fill all the fields",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val updatedMember = campusman.copy(
                        address = Address(
                            fullName = memberName.value,
                            phoneNumber = phoneNumber.value,
                            hostelNumber = hostelNumber.value,

                            ),
                        imageUrl = imageUri?.toString()
                            ?: campusman.imageUrl // Only update image if a new one is selected
                    )
                    campusManViewModel.saveUpdatedCampusman(
                        campusman = updatedMember,
                        imageUri = imageUri,
                        authViewModel = authViewModel
                    )

                }
            }
        }
    }
    if (showDeleteConfirmDialog.value) {
        when (deleteCampusmanState) {
            is StateData.Error -> {
                showDeleteConfirmDialog.value = false
                isLoadingDelete.value = false
                Toast.makeText(context, "Failed to delete", Toast.LENGTH_LONG).show()
                campusManViewModel.resetDeleteMemberState()
            }

            StateData.Idle -> {

            }

            StateData.Loading -> {
                isLoadingDelete.value = true
                LoadingDialog(isLoadingDelete)
            }

            StateData.Success -> {
                showDeleteConfirmDialog.value = false
                isLoading.value = false
                Toast.makeText(context, "Member deleted", Toast.LENGTH_LONG).show()
                onConfirmationDeletion()
                campusManViewModel.resetDeleteMemberState()
            }
        }
    }
    when (campusmanUpdateState) {
        is StateData.Error -> {
            isLoading.value = false
            Toast.makeText(
                context,
                (campusmanUpdateState as StateData.Error).message,
                Toast.LENGTH_LONG
            ).show()
            campusManViewModel.resetUpdateMemberState()
        }

        StateData.Idle -> {}
        StateData.Loading -> {
            isLoading.value = true
            LoadingDialog(isLoading)
        }

        StateData.Success -> {
            isLoading.value = false
            if (!showToast) {
                Toast.makeText(context, "Product updated successfully", Toast.LENGTH_LONG).show()
                showToast = true
                campusManViewModel.resetUpdateMemberState()
            }
        }
    }
}
