package com.oxiion.campuscart.domain.screens.adminScreens.productScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.oxiion.campuscart.domain.models.AuthViewModel


import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart.R
import com.oxiion.campuscart.data.models.productUtils.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopCampusManageProductsBar(
    topBarTitle: String,
    onBackClick: () -> Unit,
    onAddProductClick: () -> Unit
){
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
            IconButton(
                onClick = {
                    onAddProductClick()
                },
                content = {
                    Icon(
                        tint = Color(0xFF261900),
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(R.drawable.add_product),
                        contentDescription = "Search"
                    )
                }
            )
        }

    )
}

@Composable
fun ManageProductsScreen(
    viewModel: AuthViewModel,
    onAddProductClick: () -> Unit,
    onEditProductClick: (Product) -> Unit,
    onBackClick: () -> Unit
) {
    val adminData = viewModel.adminData.collectAsState()
    LaunchedEffect(Unit) {
        // Fetch new data when the screen is first shown
        viewModel.refreshAdminData()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopCampusManageProductsBar(
                topBarTitle = "Manage Products",
                onBackClick = {
                    onBackClick()
                },
                onAddProductClick = {
                    onAddProductClick()
                })
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFD8C4A0)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val count = adminData.value?.stockItems?.size
                if (count != null) {
                    items(count) { index ->
                        val product=adminData.value!!.stockItems[index]
                        ProductCard(
                            product,
                            onEditProductClick = {onEditProductClick(product)}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onEditProductClick: (Product) -> Unit) {
    Card(
        onClick = {
            onEditProductClick(product)
        },
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth()
            .height(150.dp)
            .background(Color(0xFFD8C4A0))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().background(Color(0xFF78590C))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp)
                    .background(Color(0xFF78590C))
            ) {
                Image(
                    contentScale = ContentScale.Fit,
                    painter = rememberAsyncImagePainter(model = product.image),
                    contentDescription = product.description,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }//column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .padding(start = 16.dp, top = 16.dp),
                // horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = product.name.uppercase(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Price: ${product.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Text(
                    text = "Quantity: ${product.quantity}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        }
    }
}