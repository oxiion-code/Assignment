package com.oxiion.campuscart_user.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart_user.viewmodels.CartViewModel
import com.oxiion.campuscart_user.viewmodels.OrderViewModel

@Composable
fun OrderConfirmationDialog(
    showDialog: MutableState<Boolean>,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    onNavigationBack: () -> Unit
) {
    val orderInfo by orderViewModel.orderData.collectAsState()
    val campusmanAddress by orderViewModel.campusManAddress.collectAsState()
    if (showDialog.value) {
        cartViewModel.clearCart()
        AlertDialog(
            shape = RoundedCornerShape(10.dp),
            onDismissRequest = {
                showDialog.value = false
                onNavigationBack()
            },
            containerColor = Color(0xFFE5E8ED),
            title = {
                Text(
                    text = "Order Confirmed",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF556B2F)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Order Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFC7F6C7))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF556B2F),
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ORDER CONFIRMED",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF006400)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "ID: ${orderInfo?.id}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF006400)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "CODE: ${orderInfo?.confirmationCode}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    // Campus Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFCBE6FF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "TO GET YOUR ORDER",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                "GO T0:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                            Text(
                                "Hostel: ${campusmanAddress?.hostelNumber}",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                            Text(
                                "Room No: ${campusmanAddress?.roomNumber}",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Receive order from: ${
                                    campusmanAddress?.fullName?.let { fullName ->
                                        if (fullName.contains(" ")) fullName.substringBefore(" ")
                                            .uppercase() else fullName.uppercase()
                                    } ?: "Unknown"
                                }",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                            Text(
                                "Mobile: ${campusmanAddress?.phoneNumber}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                            Text(
                                "Provide your order CODE for confirmation ",
                                modifier = Modifier.padding(top = 16.dp),
                                color = Color(0xFF22323F),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    onNavigationBack()
                }) {
                    Text("OK", color = Color(0xFF556B2F), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
