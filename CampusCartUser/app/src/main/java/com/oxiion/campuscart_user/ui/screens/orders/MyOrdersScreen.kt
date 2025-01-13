package com.oxiion.campuscart_user.ui.screens.orders

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.oxiion.campuscart_user.data.model.Order
import com.oxiion.campuscart_user.data.model.Product
import com.oxiion.campuscart_user.navigation.Screens
import com.oxiion.campuscart_user.ui.components.AppBottomBar
import com.oxiion.campuscart_user.ui.components.AppTopBar
import com.oxiion.campuscart_user.ui.screens.home.ScreenState
import com.oxiion.campuscart_user.utils.SharedPreferencesManager
import com.oxiion.campuscart_user.viewmodels.OrderViewModel

sealed class ScreenStateOrders {
    data object OrdersList : ScreenStateOrders()
    data class OrderDetails(val order: Order) : ScreenStateOrders()
}

@Composable
fun OrdersScreen(
    orderViewModel: OrderViewModel,
    navigateBack: () -> Unit,
    onNavigateToScreen: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        orderViewModel.getOrders()
    }
    val context = LocalContext.current
    val hostelNumber = SharedPreferencesManager.getHostelNumber(context)
    var currentScreen by remember { mutableStateOf<ScreenStateOrders>(ScreenStateOrders.OrdersList) }
    val ordersList by orderViewModel.ordersList.collectAsState()
    BackHandler {
        if (currentScreen is ScreenStateOrders.OrderDetails){
            currentScreen = ScreenStateOrders.OrdersList
        }else{
            navigateBack()
        }
    }
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = "My Orders",
                isHomeScreen = false,
                hostelName = hostelNumber!!,
                onBackClick = {
                    if (currentScreen is ScreenStateOrders.OrderDetails) {
                        currentScreen = ScreenStateOrders.OrdersList
                    } else {
                        navigateBack()
                    }
                }
            )
        }, bottomBar = {
            AppBottomBar(
                currentScreen = Screens.Orders.OrdersScreen.route,
                onNavigate = { route ->
                    onNavigateToScreen(route)
                }
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when(val screen =currentScreen){
                is ScreenStateOrders.OrderDetails -> {
                    OrderDetailsScreen(
                       order = screen.order,
                        onCancelOrderClick = {

                        }
                    )
                }
                ScreenStateOrders.OrdersList -> {
                    if (ordersList==null){
                        Text("No orders found")
                    }else{
                        OrdersListScreen(orders = ordersList!!,
                            onOrderCardClick = {order->
                                currentScreen=ScreenStateOrders.OrderDetails(order)
                            })
                    }

                }
            }

        }
    }
}

@Composable
fun OrdersListScreen(
    orders:List<Order>,
    onOrderCardClick: (Order) -> Unit){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(orders.reversed()) { order ->
            OrderCard(
                order = order,
                onOrderCardClick = onOrderCardClick
            )
        }
    }
}


@Composable
fun OrderCard(
    order: Order,
    onOrderCardClick: (Order) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFCBE6FF)
        ),
        onClick = {
            onOrderCardClick(order)
        }
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
                    .size(120.dp)
                    .background(Color.White)
            ) {
                val painter = rememberAsyncImagePainter(order.items[0].image)
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(110.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = order.id,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Text(
                    text = order.confirmationCode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Text(
                    text = "Total: ${order.totalPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                var color: Color = Color(0xFFFFDAD6)
                if (order.status.isDelivered) {
                    color = Color(0xFFC7F6C7)
                } else if (
                    order.status.isOnProgress
                ) {
                    color =Color.LightGray
                }
                Card(
                    modifier = Modifier.padding(2.dp), colors = CardDefaults.cardColors(
                        containerColor = color
                    )
                ) {
                    Text(
                        text = "Status: ${order.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(2.dp)
                    )
                }

            }
        }
    }
}