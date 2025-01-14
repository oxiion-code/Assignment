package com.oxiion.campuscart_user.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart_user.data.model.Order

@Composable
fun AddressCard(order: Order){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp,end=16.dp, bottom = 16.dp,top=4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFCBE6FF)
        )
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
                "Hostel: ${order.address.hostelNumber}",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Text(
                "Room No: ${order.address.roomNumber}",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Text(
                text = "Receive order from: ${
                    order.address.fullName.let { fullName ->
                        if (fullName.contains(" ")) fullName.substringBefore(" ")
                            .uppercase() else fullName.uppercase()
                    }
                }",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Text(
                "Mobile: ${order.address.phoneNumber}",
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
    } //order card
}