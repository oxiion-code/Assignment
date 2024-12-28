package com.oxiion.campuscart.domain.screens.adminScreens.campusManScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart.common.TopCampusAppBar
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.data.models.roles.CampusMan
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.domain.models.CampusManViewModel

@Composable
fun ManageStockItemsScreen(
    campusManId:String,
    onBackClick: () -> Unit,
    onAddStockItemClick: () -> Unit,
    onEditProductClick: (Product) -> Unit,
    campusManViewModel: CampusManViewModel
) {
    LaunchedEffect(campusManId) {
        campusManViewModel.fetchMemberStockItems(campusManId)
    }
    val stockItems = campusManViewModel.memberStockItems.collectAsState().value
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopCampusManageCampusMenBar(
                topBarTitle = "Stock Items",
                onBackClick = onBackClick,
                onAddMemberClick = onAddStockItemClick
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFD8C4A0)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn (
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                    items(stockItems){ stockItem->
                        ProductCard(
                            product = stockItem,
                            onEditProductClick = {
                                onEditProductClick(stockItem)
                            }
                        )
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