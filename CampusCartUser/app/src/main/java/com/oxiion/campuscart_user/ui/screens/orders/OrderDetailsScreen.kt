package com.oxiion.campuscart_user.ui.screens.orders

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart_user.data.model.Order
import com.oxiion.campuscart_user.data.model.Product
import com.oxiion.campuscart_user.ui.components.AddressCard
import com.oxiion.campuscart_user.ui.components.AppAlertBox
import com.oxiion.campuscart_user.ui.components.AppCustomBlueButton
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButton
import com.oxiion.campuscart_user.ui.components.LoadingDialogSmall
import com.oxiion.campuscart_user.ui.components.LoadingDialogTransparent
import com.oxiion.campuscart_user.ui.screens.home.ProductCard
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.viewmodels.OrderViewModel

@Composable
fun OrderDetailsScreen(
    orderViewModel: OrderViewModel,
    order: Order,
    onCancelOrderClick: (Order) -> Unit,
    navigateBack:()->Unit
) {
    val cancellationDialog= remember { mutableStateOf(false) }
    val orderCancelState by orderViewModel.cancelOrderState.collectAsState()
    val context= LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    val isDelivered=remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Delivery Address Card
        // Product List
        items(order.items) { order ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp,top = 4.dp, bottom = 4.dp)
                    .background(Color(0xFF29638A)),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF29638A),
                    contentColor = Color.White
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp,100.dp)
                            .background(Color.White)
                    ) {
                        val painter = rememberAsyncImagePainter(order.image)
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(70.dp,80.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = order.name,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = "Price: ${order.discount}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = "Quantity: ${order.quantity}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }////

            }
        }

        // Price and Order ID Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=8.dp,start = 16.dp, end = 16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFDAD6),
                    contentColor = Color.Black
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Price: ₹${order.totalPrice}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Order ID: ${order.id}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Code: ${order.confirmationCode}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            AddressCard(order)
        }
        item{
            Text("The cancellation amount will be added to your WALLET\n Use it for your next order!",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4E0002),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
           AppCustomBlueButton(
               text = "Cancel order",
               onClick = {
                   cancellationDialog.value=true
               }
           )
        }
    }
    if (cancellationDialog.value){
        AppAlertBox(
            onConfirm = {
                onCancelOrderClick(order)
                cancellationDialog.value=false
            },
            onDismiss = {
                cancellationDialog.value=false
            },
            title = "Cancel order",
            message = "Do you confirm the cancellation of this order?"
        )
    }
    if (isLoading.value){
        LoadingDialogTransparent(isLoading)
    }
    when(orderCancelState){
        is DataState.Error -> {
            isLoading.value=false
            Toast.makeText(context, (orderCancelState as DataState.Error).message, Toast.LENGTH_SHORT).show()
            orderViewModel.resetOrderCancellationState()
        }
        DataState.Idle -> {
        }
        DataState.Loading -> {
            isLoading.value=true
        }
        DataState.Success -> {
            isLoading.value=false
            Toast.makeText(context, "Order canceled", Toast.LENGTH_SHORT).show()
            orderViewModel.resetOrderCancellationState()
            navigateBack()
        }
    }
}

// Sample Data Classes
