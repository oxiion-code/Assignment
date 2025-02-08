package com.oxiion.campuscart_user.ui.screens.cart

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart_user.navigation.Screens
import com.oxiion.campuscart_user.ui.components.AppBottomBar
import com.oxiion.campuscart_user.ui.components.AppTopBar
import com.oxiion.campuscart_user.viewmodels.AuthViewModel
import com.oxiion.campuscart_user.viewmodels.CartViewModel
import com.oxiion.campuscart_user.data.datasource.local.CartItem
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.utils.SharedPreferencesManager
import com.oxiion.campuscart_user.viewmodels.OrderViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    orderViewModel: OrderViewModel,
    navigateToScreen: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val college = SharedPreferencesManager.getCollege(context)!!
    val hostel = SharedPreferencesManager.getHostelNumber(context)!!
    val userData by authViewModel.userData.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val discountedPrice by cartViewModel.discountedPrice.collectAsState()
    val checkOrdersAvailabilityState by orderViewModel.checkOrdersAvailabilityState.collectAsState()

    BackHandler { navigateBack() }

    LaunchedEffect(Unit) {
        cartViewModel.loadCartItems()
        cartViewModel.refreshTotalPrice()
        cartViewModel.refreshDisCountedPrice()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(Color.White),
        topBar = {
            userData.address?.let {
                AppTopBar(
                    title = "Cart",
                    isHomeScreen = false,
                    hostelName = it.hostelNumber,
                    onBackClick = navigateBack
                )
            }
        },
        bottomBar = {
            AppBottomBar(
                currentScreen = Screens.Cart.CartScreen.route,
                onNavigate = navigateToScreen
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            if (cartItems.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cartItems) { cartItem ->
                        CartItemView(
                            cartItem = cartItem,
                            onRemoveCartItemClick = { item ->
                                cartViewModel.removeFromCart(item)
                            },
                            onChangeItemQuantityClick = { item ->
                                cartViewModel.updateCartItem(item)
                            }
                        )
                    }
                }
            } else {
                Text(
                    text = "Your cart is empty",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.background(Color.White)) {
                    Text(
                        text = "Total MRP (Incl. of taxes): ₹$totalPrice",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Blue
                    )
                    Text(
                        text = "Discounted Amount: ₹$discountedPrice",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF00476B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            authViewModel.fetchProductList(college, hostel)
                            orderViewModel.checkOrdersAvailability(cartItems)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        enabled = totalPrice > 0.0 && checkOrdersAvailabilityState !is DataState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF29638A),
                            contentColor = Color.White
                        )
                    ) {
                        if (checkOrdersAvailabilityState is DataState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Pay ₹$discountedPrice",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }

    // Observe availability check result
    LaunchedEffect(checkOrdersAvailabilityState) {
        when (checkOrdersAvailabilityState) {
            is DataState.Success -> {
                navigateToScreen(Screens.Payment.PaymentScreen.route)
                orderViewModel.resetCheckOrderAvailabilityState()
            }
            is DataState.Error -> {
                Toast.makeText(context, "Some products are no longer available", Toast.LENGTH_SHORT).show()
                orderViewModel.resetCheckOrderAvailabilityState()
            }
            else -> {} // Do nothing for Idle or Loading states
        }
    }
}


@Composable
fun CartItemView(
    cartItem: CartItem,
    onRemoveCartItemClick: (CartItem) -> Unit,
    onChangeItemQuantityClick: (CartItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // For quantity dropdown menu

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC1C7CE)) // Gray background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                val painter = rememberAsyncImagePainter(cartItem.image)
                Image(
                    painter = painter,
                    contentDescription = "Cart Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(70.dp, 100.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = cartItem.productName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight(600)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Price: ₹${cartItem.price * cartItem.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Text(
                    text = "Discounted Price: ₹${cartItem.discountedPrice?.times(cartItem.quantity)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Quantity Selector
                    Box {
                        Card(
                            shape = RoundedCornerShape(4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEBDCFF)),
                            onClick = { expanded = true }
                        ) {
                            Text(
                                text = "Qty: ${cartItem.quantity}",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        }
                        DropdownMenu(
                            modifier = Modifier.background(Color.Black).height(100.dp),
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            (1..5).forEach { qty ->
                                DropdownMenuItem(
                                    text = { Text("Qty: $qty",
                                        color = Color.White) },
                                    onClick = {
                                        expanded = false
                                        onChangeItemQuantityClick(
                                            cartItem.copy(quantity = qty)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Remove Item
                    Card(
                        shape = RoundedCornerShape(4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEBDCFF)),
                        onClick = { onRemoveCartItemClick(cartItem) }
                    ) {
                        Text(
                            text = "Remove",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF8C0009)
                        )
                    }
                }
            }
        }
    }
}


