package com.oxiion.campuscart_user.ui.screens.orders

import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.oxiion.campuscart_user.navigation.Screens
import com.oxiion.campuscart_user.ui.components.AppBottomBar
import com.oxiion.campuscart_user.ui.components.AppTopBar
import com.oxiion.campuscart_user.ui.components.LoadingDialogTransparent
import com.oxiion.campuscart_user.ui.screens.home.ScreenState
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.utils.SharedPreferencesManager
import com.oxiion.campuscart_user.viewmodels.AuthViewModel
import com.oxiion.campuscart_user.viewmodels.OrderViewModel

sealed class ScreenStateOrders {
    data object OrdersList : ScreenStateOrders()
    data class OrderDetails(val order: Order) : ScreenStateOrders()
}

@Composable
fun OrdersScreen(
    authViewModel: AuthViewModel,
    orderViewModel: OrderViewModel,
    navigateBack: () -> Unit,
    onNavigateToScreen: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        orderViewModel.getOrders()
    }
    val context = LocalContext.current
    val hostelNumber = SharedPreferencesManager.getHostelNumber(context) ?: "Hostel"
    val uid = SharedPreferencesManager.getUid(context) ?: return
    var currentScreen by remember { mutableStateOf<ScreenStateOrders>(ScreenStateOrders.OrdersList) }
    val ordersList by orderViewModel.ordersList.collectAsState()
    val getOrdersState by orderViewModel.getOrdersState.collectAsState()
    val isLoading = remember { mutableStateOf(false) }

    BackHandler {
        if (currentScreen is ScreenStateOrders.OrderDetails) {
            currentScreen = ScreenStateOrders.OrdersList
        } else {
            navigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
        topBar = {
            AppTopBar(
                title = "My Orders",
                isHomeScreen = false,
                hostelName = hostelNumber,
                onBackClick = {
                    if (currentScreen is ScreenStateOrders.OrderDetails) {
                        currentScreen = ScreenStateOrders.OrdersList
                    } else {
                        navigateBack()
                    }
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                currentScreen = Screens.Orders.OrdersScreen.route,
                onNavigate = { route -> onNavigateToScreen(route) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when (val screen = currentScreen) {
                is ScreenStateOrders.OrderDetails -> {
                    OrderDetailsScreen(
                        orderViewModel = orderViewModel,
                        order = screen.order,
                        onCancelOrderClick = { order ->
                            orderViewModel.cancelOrder(order.id)
                        },
                        navigateBack = {
                            authViewModel.fetchUserData(uid)
                            orderViewModel.getOrders()
                            currentScreen = ScreenStateOrders.OrdersList
                        }
                    )
                }

                ScreenStateOrders.OrdersList -> {
                    if (ordersList.isNullOrEmpty()) {
                        Text(
                            text = "No orders found",
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    } else {
                        OrdersListScreen(
                            orders = ordersList!!,
                            onOrderCardClick = { order ->
                                currentScreen = ScreenStateOrders.OrderDetails(order)
                            }
                        )
                    }
                }
            }
        }

        // Handle Loading State
        when (getOrdersState) {
            is DataState.Loading -> {
                isLoading.value = true
            }

            is DataState.Error -> {
                isLoading.value = false
                Toast.makeText(
                    context,
                    (getOrdersState as DataState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is DataState.Success -> {
                isLoading.value = false
            }

            else -> Unit
        }
    }

    // Show Loading Dialog
    if (isLoading.value) {
        LoadingDialogTransparent(isLoading)
    }
}


@Composable
fun OrdersListScreen(
    orders: List<Order>,
    onOrderCardClick: (Order) -> Unit
) {
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
                    .size(80.dp, 100.dp)
                    .background(Color.White)
            ) {
                // Check if order.items is not empty before accessing the first item
                if (order.items.isNotEmpty()) {
                    val painter = rememberAsyncImagePainter(order.items[0].image)
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(70.dp, 80.dp)
                    )
                } else {
                    // Show a placeholder or a default image if the list is empty
                    Box(
                        modifier = Modifier
                            .size(70.dp, 80.dp)
                            .background(Color.Gray)
                    ) {
                        Text(
                            text = "No Image",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
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
                    text = "Code:${order.confirmationCode}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Text(
                    text = "Total: ${order.totalPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                val (color, showText) = when {
                    order.status.delivered-> Pair(Color(0xFFC7F6C7), "Delivered")
                    order.status.cancelled -> Pair(Color(0xFFFFDAD6), "Cancelled")
                    order.status.onProgress -> Pair(Color.LightGray, "In Progress")
                    else -> Pair(Color(0xFFEAEAEA), "Unknown") // Default fallback case
                }
                Card(
                    modifier = Modifier.padding(top = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = color
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = showText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}