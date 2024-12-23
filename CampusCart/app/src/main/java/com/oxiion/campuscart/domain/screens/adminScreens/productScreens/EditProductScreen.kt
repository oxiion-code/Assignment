package com.oxiion.campuscart.domain.screens.adminScreens.productScreens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.domain.models.ProductViewModel
import com.oxiion.campuscart.ui_app.components.AppTextBox
import com.oxiion.campuscart.ui_app.components.CustomBlackGreenTextBox
import com.oxiion.campuscart.ui_app.components.CustomButton
import com.oxiion.campuscart.utils.StateData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopCampusEditTaskAppBar(
    topBarTitle: String,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
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
                    onDeleteClick()
                },
                content = {
                    Icon(
                        tint = Color(0xFF261900),
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Search"
                    )
                }
            )
        }

    )
}

@Composable
fun EditProductScreen(
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    product: Product,
    onBackClick: () -> Unit,
    onConfirmDeletion: () -> Unit
) {
    val productUpdateState by productViewModel.updateProductState.collectAsState()
    val deleteProductState by productViewModel.deleteProductState.collectAsState()
    val context = LocalContext.current

    val isLoading = remember { mutableStateOf(false) }
    val isLoadingDelete=remember { mutableStateOf(false)}

    val productName = remember { mutableStateOf(product.name) }
    val productCount = remember { mutableStateOf(product.quantity.toString()) }
    val productPrice = remember { mutableStateOf(product.price.toString()) }
    val productDescription = remember { mutableStateOf(product.description) }

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
                AppExitDialog(onDismiss = {
                    showDeleteConfirmDialog.value = false
                }, onConfirm = {
                    productViewModel.deleteProduct(product, authViewModel)
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
                        name = productName.value,
                        category = productDescription.value,
                        quantity = productCount.value.toIntOrNull() ?: 0,
                        description = productDescription.value,
                        price = productPrice.value.toDoubleOrNull() ?: 0.0,
                        image = imageUri?.toString()
                            ?: product.image // Only update image if a new one is selected
                    )
                    productViewModel.saveUpdatedProduct(
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
                showDeleteConfirmDialog.value=false
                isLoadingDelete.value=false
                Toast.makeText(context, "Product deleted", Toast.LENGTH_LONG).show()
                productViewModel.resetDeleteProductState()
            }

            StateData.Idle -> {

            }

            StateData.Loading -> {
                isLoadingDelete.value=true
                LoadingDialog(isLoadingDelete)
            }

            StateData.Success -> {
                showDeleteConfirmDialog.value=false
                isLoading.value=false
                Toast.makeText(context, "Product deleted", Toast.LENGTH_LONG).show()
                onConfirmDeletion()
                productViewModel.resetDeleteProductState()
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
            productViewModel.resetUpdateProductState()
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
                productViewModel.resetUpdateProductState()
            }
        }
    }
}
