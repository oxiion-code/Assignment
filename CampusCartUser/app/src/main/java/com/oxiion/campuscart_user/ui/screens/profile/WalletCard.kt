package com.oxiion.campuscart_user.ui.screens.profile

import android.service.quickaccesswallet.WalletCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.core.UserData
import com.oxiion.campuscart_user.R
import com.oxiion.campuscart_user.data.model.User
import com.oxiion.campuscart_user.ui.theme.backgroundLight
import com.oxiion.campuscart_user.ui.theme.primaryLight

@Composable
fun WalletCard(userData: User){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundLight,
            contentColor = primaryLight
        ),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.user_wallet),
                contentDescription = "Wallet",
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
            )
            Text(
                "Wallet: ₹${userData.walletMoney}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}