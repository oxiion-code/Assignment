package com.oxiion.campuscart_user.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart_user.data.model.User
import com.oxiion.campuscart_user.ui.theme.backgroundLight
import com.oxiion.campuscart_user.ui.theme.primaryLight
import com.oxiion.campuscart_user.ui.theme.secondaryLight
import com.oxiion.campuscart_user.ui.theme.tertiaryLight

@Composable
fun AboutMeCard(userData:User,changeAddressScreen:()->Unit){
    Text(
        text = "About Me", color = secondaryLight,
        modifier = Modifier.padding(top = 1.dp, start = 16.dp, bottom = 8.dp),
        style = MaterialTheme.typography.bodySmall
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundLight,
            contentColor = tertiaryLight
        ),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Name: ${userData.address?.fullName}",
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Email: ${userData.address?.email}",
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Phone: ${userData.address?.phoneNumber}",
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "College: ${userData.college}",
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Hostel: ${userData.address?.hostelNumber}",
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Correct Me", style = MaterialTheme.typography.titleSmall,
                    color = primaryLight,
                    fontWeight = FontWeight(600),
                    modifier = Modifier.clickable {
                        changeAddressScreen()
                    },
                )
            }
        }

    }
}