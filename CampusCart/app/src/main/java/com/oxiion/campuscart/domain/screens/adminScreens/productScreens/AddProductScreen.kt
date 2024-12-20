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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart.R
import com.oxiion.campuscart.common.LoadingDialog
import com.oxiion.campuscart.common.TopCampusAppBar
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.domain.models.AdminViewModel
import com.oxiion.campuscart.ui_app.components.CustomBlackGreenTextBox
import com.oxiion.campuscart.ui_app.components.CustomButton
import com.oxiion.campuscart.utils.LoginState

@Composable
fun AddProductScreen(productViewModel: AddProductViewModel= viewModel(),adminViewModel: AdminViewModel) {
    val productAddState by adminViewModel.addProductState.collectAsState()
    val context = LocalContext.current
    val isLoading= remember { mutableStateOf(false) }
    val productName= remember { mutableStateOf("") }
    val productCount= remember { mutableStateOf("") }
    val productPrice= remember { mutableStateOf("") }
    val productDescription= remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val uploadedImageUrl by productViewModel.uploadedImageUrl.collectAsState()
    val launcher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ){uri: Uri? ->
        uri?.let {
            imageUri=it
            productViewModel.uploadImageToFirebase(it)
        }
    }

    Scaffold(
        topBar = {
            TopCampusAppBar(
                topBarTitle = "Add product",
                onBackClick = {},
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFD8C4A0)).imePadding(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn (
                horizontalAlignment = Alignment.CenterHorizontally){
                item {
                    Image(
                        painter = if (uploadedImageUrl!=null){
                            rememberAsyncImagePainter(uploadedImageUrl)
                        }else if (imageUri  != null){
                            rememberAsyncImagePainter(imageUri)
                        }else{
                            painterResource(R.drawable.default_image)
                        },
                        contentDescription = "Product Image",
                        modifier = Modifier.
                        size(200.dp).
                        background(Color.Gray, RoundedCornerShape(10.dp)),
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
                    CustomBlackGreenTextBox(text = productCount, placeholder = "Enter total product count")
                }
                item {
                    CustomBlackGreenTextBox(text = productPrice, placeholder = "Enter product price")
                }
                item {
                    CustomBlackGreenTextBox(text = productDescription, placeholder = "Enter product description")
                }
            }
            CustomAddButton("Add Product") {
                val product=Product(
                    id = productName.value+productCount.value,
                    name = productName.value,
                    category = productDescription.value,
                    quantity = productCount.value.toInt(),
                    rating =0.0, // For example, you can get the rating from user input or set it
                    isAvailable = true, // Example, you can set this based on user input
                    discount =0.0,
                    description = productDescription.value,
                    price = productPrice.value.toDouble(),
                    image = uploadedImageUrl.toString() // The URL of the uploade
                )
                adminViewModel.addProduct(product)
            }
        }
        when(productAddState){
            is LoginState.Error -> {
                isLoading.value=false
                Toast.makeText(context, "Product is not added", Toast.LENGTH_LONG).show()
            }
            LoginState.Idle -> {}
            LoginState.Loading -> {
                isLoading.value=true
                LoadingDialog(isLoading)
            }
            LoginState.Success -> {
                isLoading.value=false
                Toast.makeText(context, "Product is added", Toast.LENGTH_LONG).show()
            }
        }
    }
}
@Composable
fun CustomAddButton(text: String, onButtonClick: () -> Unit) {
    Button(
        onClick = {
            onButtonClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF402D00),
            contentColor = Color(0xFFD8C4A0)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier =  Modifier.size(width = 300.dp, height = 46.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 16.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}