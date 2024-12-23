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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart.R
import com.oxiion.campuscart.common.LoadingDialog
import com.oxiion.campuscart.common.TopCampusAppBar
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.domain.models.ProductViewModel
import com.oxiion.campuscart.ui_app.components.CustomBlackGreenTextBox
import com.oxiion.campuscart.ui_app.components.CustomButton
import com.oxiion.campuscart.utils.LoginState
import com.oxiion.campuscart.utils.StateData

@Composable
fun AddProductScreen(
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    onBackClick: () -> Unit
) {
    val productAddState by productViewModel.addProductState.collectAsState()
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    val productName = remember { mutableStateOf("") }
    val productCount = remember { mutableStateOf("") }
    val productPrice = remember { mutableStateOf("") }
    val productDescription = remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showToast by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it }
    }

    Scaffold(
        topBar = {
            TopCampusAppBar(
                topBarTitle = "Add Product",
                onBackClick = onBackClick,
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
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    Image(
                        painter = if (imageUri != null)
                            rememberAsyncImagePainter(imageUri)
                        else painterResource(R.drawable.default_image),
                        contentDescription = "Product Image",
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
                    CustomBlackGreenTextBox(text = productName, placeholder = "Enter product name")
                }
                item {
                    CustomBlackGreenTextBox(
                        text = productCount,
                        placeholder = "Enter total product count"
                    )
                }
                item {
                    CustomBlackGreenTextBox(
                        text = productPrice,
                        placeholder = "Enter product price"
                    )
                }
                item {
                    CustomBlackGreenTextBox(
                        text = productDescription,
                        placeholder = "Enter product description"
                    )
                }
            }
            CustomButton("Add Product") {
                if (productName.value.isBlank() ||
                    productCount.value.isBlank() ||
                    productPrice.value.isBlank() ||
                    productDescription.value.isBlank() ||
                    imageUri == null
                ) {
                    Toast.makeText(
                        context,
                        "Please fill all the fields and upload an image",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val product = Product(
                        id = "${productName.value}_${productCount.value}",
                        name = productName.value,
                        category = productDescription.value,
                        quantity = productCount.value.toIntOrNull() ?: 0,
                        rating = 0.0,
                        isAvailable = true,
                        discount = 0.0,
                        description = productDescription.value,
                        price = productPrice.value.toDoubleOrNull() ?: 0.0,
                        image = ""
                    )
                    productViewModel.addProductWithImage(imageUri!!, product, authViewModel)
                }
            }
        }
    }

    when (productAddState) {
        is StateData.Error -> {
            isLoading.value = false
            Toast.makeText(
                context,
                (productAddState as LoginState.Error).message ?: "Unknown Error",
                Toast.LENGTH_LONG
            ).show()
            productViewModel.resetAddProductState()
        }
        StateData.Idle -> {}
        StateData.Loading -> {
            isLoading.value = true
            LoadingDialog(isLoading)
        }
        StateData.Success -> {
            isLoading.value = false
            if (!showToast) {
                Toast.makeText(context, "Product added successfully", Toast.LENGTH_LONG).show()
                showToast = true

                productName.value = ""
                productCount.value = ""
                productPrice.value = ""
                productDescription.value = ""
                imageUri = null

                productViewModel.resetAddProductState()
            }
        }
    }
}
