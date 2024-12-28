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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart.common.AppExitDialog
import com.oxiion.campuscart.common.LoadingDialog
import com.oxiion.campuscart.common.TopCampusAppBar
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.domain.models.CampusManViewModel
import com.oxiion.campuscart.domain.screens.adminScreens.productScreens.TopCampusEditTaskAppBar
import com.oxiion.campuscart.ui_app.components.CustomBlackGreenTextBox
import com.oxiion.campuscart.ui_app.components.CustomButton
import com.oxiion.campuscart.utils.StateData
@Composable
fun EditStockItemScreen(
    authViewModel: AuthViewModel,
    campusManViewModel: CampusManViewModel,
    productId: String,
    onBackClick: () -> Unit,
    onConfirmDeleteClick: () -> Unit,
    memberId: String
) {
    val stockItems by campusManViewModel.memberStockItems.collectAsState(emptyList())

    // Find the product with the given productId, handle null check for product
    val product = stockItems.find { it.id == productId }

    // Null safety check for product
    if (product == null) {
        // Optionally, you can show an error message if the product is not found
        return // exit the composable early if product is null
    }

    val productUpdateState by campusManViewModel.updateStockItemState.collectAsState()
    val deleteProductState by campusManViewModel.deleteStockItemState.collectAsState()
    val context = LocalContext.current

    val isLoading = remember { mutableStateOf(false) }
    val isLoadingDelete = remember { mutableStateOf(false) }

    // Initialize product fields safely
    val productName = remember { mutableStateOf(product.name ?: "") }
    val productCount = remember { mutableStateOf(product.quantity?.toString() ?: "") }
    val productPrice = remember { mutableStateOf(product.price?.toString() ?: "") }
    val productDescription = remember { mutableStateOf(product.description ?: "") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showToast by remember { mutableStateOf(false) }
    val showDeleteConfirmDialog = remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { imageUri = it } }

    Scaffold(
        topBar = {
            TopCampusEditTaskAppBar(
                topBarTitle = "Edit Product",
                onBackClick = onBackClick,
                onDeleteClick = {
                    showDeleteConfirmDialog.value = true
                }
            )
        },
        modifier = Modifier.fillMaxSize()
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
                AppExitDialog(
                    onDismiss = { showDeleteConfirmDialog.value = false },
                    onConfirm = {
                        campusManViewModel.deleteStockItem(productId = productId, campusManId = memberId)
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
                        else rememberAsyncImagePainter(product.image),
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
                    CustomBlackGreenTextBox(text = productName, placeholder = "Edit product name")
                }
                item {
                    CustomBlackGreenTextBox(
                        text = productCount,
                        placeholder = "Edit total product count"
                    )
                }
                item {
                    CustomBlackGreenTextBox(
                        text = productPrice,
                        placeholder = "Edit product price"
                    )
                }
                item {
                    CustomBlackGreenTextBox(
                        text = productDescription,
                        placeholder = "Edit product description"
                    )
                }
            }
            CustomButton("Save Changes") {
                // Null checks for the fields
                if (productName.value.isBlank() ||
                    productCount.value.isBlank() ||
                    productPrice.value.isBlank() ||
                    productDescription.value.isBlank()
                ) {
                    Toast.makeText(
                        context,
                        "Please fill all the fields",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val updatedProduct = product.copy(
                        name = productName.value.takeIf { it.isNotBlank() } ?: product.name,
                        category = productDescription.value.takeIf { it.isNotBlank() } ?: product.category,
                        quantity = productCount.value.toIntOrNull() ?: product.quantity,
                        description = productDescription.value.takeIf { it.isNotBlank() } ?: product.description,
                        price = productPrice.value.toDoubleOrNull() ?: product.price,
                        image = imageUri?.toString() ?: product.image // Only update image if a new one is selected
                    )
                    campusManViewModel.saveUpdatedProduct(
                        campusManId = memberId,
                        product = updatedProduct,
                        imageUri = imageUri,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }

    if (showDeleteConfirmDialog.value) {
        when (deleteProductState) {
            is StateData.Error -> {
                showDeleteConfirmDialog.value = false
                isLoadingDelete.value = false
                Toast.makeText(context, "Product deleted", Toast.LENGTH_LONG).show()
                campusManViewModel.resetDeleteProductState()
            }

            StateData.Idle -> {}

            StateData.Loading -> {
                isLoadingDelete.value = true
                LoadingDialog(isLoadingDelete)
            }

            StateData.Success -> {
                showDeleteConfirmDialog.value = false
                isLoading.value = false
                Toast.makeText(context, "Product deleted", Toast.LENGTH_LONG).show()
                onConfirmDeleteClick()
                campusManViewModel.resetDeleteProductState()
            }
        }
    }

    when (productUpdateState) {
        is StateData.Error -> {
            isLoading.value = false
            Toast.makeText(
                context,
                (productUpdateState as StateData.Error).message,
                Toast.LENGTH_LONG
            ).show()
            campusManViewModel.resetProductUpdateState()
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
                campusManViewModel.resetProductUpdateState()
            }
        }
    }
}
