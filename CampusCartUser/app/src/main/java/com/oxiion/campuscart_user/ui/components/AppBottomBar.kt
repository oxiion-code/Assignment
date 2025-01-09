package com.oxiion.campuscart_user.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart_user.R
import com.oxiion.campuscart_user.navigation.Screens
@Composable
fun AppBottomBar(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    BottomAppBar(
        modifier = Modifier
            .height(65.dp),
        containerColor = Color(0xFF181C20),
        tonalElevation = BottomAppBarDefaults.ContainerElevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Icon
            BottomBarIcon(
                screen = Screens.Home.HomeScreen.route,
                currentScreen = currentScreen,
                icon = Icons.Filled.Home,
                onClick = onNavigate
            )
            // Cart Icon
            BottomBarIcon(
                screen = Screens.Cart.CartScreen.route,
                currentScreen = currentScreen,
                icon = Icons.Filled.ShoppingCart,
                onClick = onNavigate
            )
            // Orders Icon (using painter for drawable resource)
            BottomBarIcon(
                screen = Screens.Orders.OrdersScreen.route,
                currentScreen = currentScreen,
                iconPainter = painterResource(R.drawable.baseline_receipt_long_24),
                onClick = onNavigate
            )
            // Profile Icon
            BottomBarIcon(
                screen = Screens.Profile.ProfileScreen.route,
                currentScreen = currentScreen,
                icon = Icons.Filled.Person,
                onClick = onNavigate
            )
        }
    }
}

@Composable
fun BottomBarIcon(
    screen: String,
    currentScreen: String,
    icon: ImageVector? = null,
    iconPainter: androidx.compose.ui.graphics.painter.Painter? = null,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick(screen) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = screen,
                tint = if (currentScreen == screen) Color(0xFF29638A) else Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else if (iconPainter != null) {
            Icon(
                painter = iconPainter,
                contentDescription = screen,
                tint = if (currentScreen == screen) Color(0xFF29638A) else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        // Text under the icon
        Text(
            text = when (screen) {
                Screens.Home.HomeScreen.route -> "Home"
                Screens.Cart.CartScreen.route -> "Cart"
                Screens.Orders.OrdersScreen.route -> "Orders"
                Screens.Profile.ProfileScreen.route -> "Profile"
                else -> ""
            },
            fontSize = 12.sp,
            color = if (currentScreen == screen) Color(0xFF29638A) else Color.White,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

