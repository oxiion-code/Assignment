package com.oxiion.campuscart_user.ui.screens.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart_user.Constants
import com.oxiion.campuscart_user.R
import com.oxiion.campuscart_user.ui.components.LoadingDialogSmall
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.utils.SharedPreferencesManager
import com.oxiion.campuscart_user.viewmodels.AuthViewModel
import com.oxiion.campuscart_user.viewmodels.CartViewModel
import com.oxiion.campuscart_user.viewmodels.OrderViewModel
import com.oxiion.campuscart_user.viewmodels.PaymentViewModel
import com.phonepe.intent.sdk.api.*
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*

@SuppressLint("MutableCollectionMutableState")
@Composable
fun PaymentScreen(
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    paymentViewModel: PaymentViewModel = viewModel(),
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    onNavigationBack: () -> Unit
) {
    val context = LocalContext.current
    val upiAppsSdk: List<UPIApplicationInfo> by remember { mutableStateOf(PhonePe.getUpiApps()) }
    var selectedPackageName by remember { mutableStateOf<String?>(null) }
    var paymentProcessed by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val totalAmount by cartViewModel.discountedPrice.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val paymentVerificationStatus by paymentViewModel.paymentVerificationStatus.collectAsState()
    val orderCreationStatus by orderViewModel.orderCreationState.collectAsState()
    val isLoading = remember { mutableStateOf(false) }
    val userData by authViewModel.userData.collectAsState()
    val showConfirmationOrder=remember { mutableStateOf(false)}

    // State for wallet usage
    var useWalletMoney by remember { mutableStateOf(false) }
    val walletMoney = userData.walletMoney.takeIf { it > 0 } ?: 0.0
    val walletDeduction = if (useWalletMoney) minOf(totalAmount, walletMoney) else 0.0
    val payableAmount = totalAmount - walletDeduction
    val userUid=SharedPreferencesManager.getUid(context)
    LaunchedEffect(Unit) {
        authViewModel.fetchUserData(userUid!!)
    }
    // PhonePe Launcher
    val phonePeLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            isProcessing = false
            if (result.resultCode == Activity.RESULT_OK) {
                checkStatus(paymentViewModel)
            } else {
                Toast.makeText(context, "Payment canceled or failed!", Toast.LENGTH_SHORT).show()
            }
        }

    // Payment verification observer
    if (paymentVerificationStatus != null && !paymentProcessed) {
        if (paymentVerificationStatus == true) {
            orderViewModel.createOrder(products = cartItems, totalPrice = totalAmount, "")
            Toast.makeText(context, "Payment successful!", Toast.LENGTH_SHORT).show()
            paymentViewModel.resetPaymentStatus()
        } else {
            Toast.makeText(context, "Payment failed or not verified!", Toast.LENGTH_SHORT).show()
        }
        paymentProcessed = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFFEEF1F6)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF97CCF8)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total Amount: ₹$totalAmount",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
                if (useWalletMoney) {
                    Text(
                        text = "Wallet Money Used: ₹$walletDeduction",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
                Text(
                    text = "Payable Amount: ₹$payableAmount",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Use Wallet Money",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp) // Add some padding for a sleeker look
                            .size(width = 50.dp, height = 24.dp) // Adjust width and height
                    ) {
                        Switch(
                            checked = useWalletMoney,
                            onCheckedChange = { useWalletMoney = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF0056B3), // Deep blue for the checked thumb
                                uncheckedThumbColor = Color(0xFFB0C4DE), // Light steel blue for the unchecked thumb
                                checkedTrackColor = Color(0xFF6A9EFF), // Sky blue for the checked track
                                uncheckedTrackColor = Color(0xFFD3D3D3), // Light gray for the unchecked track
                                disabledCheckedThumbColor = Color(0xFF80A4E0), // Muted blue for disabled checked thumb
                                disabledUncheckedThumbColor = Color(0xFFCCCCCC) // Soft gray for disabled unchecked thumb
                            ),
                            modifier = Modifier.scale(0.8f) // Scale down the Switch for a narrower appearance
                        )
                    }

                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(upiAppsSdk) { appInfo ->
                val appIcon = getAppIcon(context, appInfo.packageName)
                PaymentApp(
                    app = appInfo,
                    icon = appIcon,
                    isSelected = appInfo.packageName == selectedPackageName,
                    onClick = { selectedPackageName = appInfo.packageName }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (payableAmount == 0.0) {
                    // Directly create the order as no payment is required
                    orderViewModel.createOrder(products = cartItems, totalPrice = totalAmount, "")
                    Toast.makeText(context, "Order created successfully!", Toast.LENGTH_SHORT).show()
                    showConfirmationOrder.value=true
                } else if (selectedPackageName != null) {
                    paymentProcessed = false
                    isProcessing = true
                    paymentViewModel.resetPaymentStatus()

                    initializePhonePe(
                        context = context,
                        amount = payableAmount * 100,
                        phonePeLauncher = phonePeLauncher,
                        selectedPackage = selectedPackageName!!
                    )
                } else {
                    Toast.makeText(context, "Select a UPI app", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isProcessing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF437AA2),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = "Pay Now", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (showConfirmationOrder.value){
            OrderConfirmationDialog(showConfirmationOrder,cartViewModel,orderViewModel,onNavigationBack)
        }
        when (orderCreationStatus) {
            is DataState.Error -> {
                isLoading.value = false
                Toast.makeText(
                    context,
                    (orderCreationStatus as DataState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                orderViewModel.resetOrderCreationState()
            }
            DataState.Idle -> {}
            DataState.Loading -> {
                isLoading.value = true
                LoadingDialogSmall(isLoading)
            }
            DataState.Success -> {
                isLoading.value = false
                showConfirmationOrder.value=true
                orderViewModel.resetOrderCreationState()
            }
        }
    }
}
@Composable
fun PaymentApp(
    app: UPIApplicationInfo,
    icon: Drawable?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)  // Reduce the card height to make them smaller
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.Blue else Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val fallbackIcon =
                painterResource(id = R.drawable.ic_launcher_foreground)  // Fallback icon
            val painter = icon?.let { rememberAsyncImagePainter(it) } ?: fallbackIcon

            Icon(
                painter = painter,
                contentDescription = app.applicationName,
                modifier = Modifier.size(40.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = app.applicationName,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}

private fun getAppIcon(context: Context, packageName: String): Drawable? {
    val packageManager = context.packageManager
    return try {
        packageManager.getApplicationIcon(packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }
}

private fun initializePhonePe(
    context: Context,
    amount: Double,
    phonePeLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    selectedPackage: String
) {
    val transactionId = generateTransactionId()
    Constants.merchantTransactionId = transactionId

    val data = JSONObject().apply {
        put("merchantId", Constants.MERCHANT_ID)
        put("merchantTransactionId", transactionId)
        put("amount", amount)
        put("mobileNumber", "9999999999")
        put("callbackUrl", "https://webhook.site/c877bd21-55b3-4135-bf43-88ab7a281594")
        put("paymentInstrument", JSONObject().apply {
            put("type", "UPI_INTENT")
            put("targetApp", selectedPackage)
        })
        put("deviceContext", JSONObject().apply {
            put("deviceOS", "ANDROID")
        })
    }

    val payloadBase64 =
        Base64.encodeToString(data.toString().toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    val checksum = sha256(payloadBase64 + Constants.apiEndPoint + Constants.SALT_KEY) + "###1"

    val b2BPGRequest = B2BPGRequestBuilder()
        .setData(payloadBase64)
        .setChecksum(checksum)
        .setUrl(Constants.apiEndPoint)
        .build()

    getPaymentView(b2BPGRequest, context, phonePeLauncher, selectedPackage)
}

fun getPaymentView(
    request: B2BPGRequest,
    context: Context,
    phonePeLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    selectedPackage: String
) {
    try {
        PhonePe.getImplicitIntent(context, request, selectedPackage)?.let { intent ->
            phonePeLauncher.launch(intent)
        }
    } catch (e: PhonePeInitException) {
        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
    }
}

private fun checkStatus(viewModel: PaymentViewModel) {
    val xVerify = sha256(
        "/pg/v1/status/${Constants.MERCHANT_ID}/${Constants.merchantTransactionId}${Constants.SALT_KEY}"
    ) + "###1"
    val headers = mapOf(
        "Content-Type" to "application/json",
        "X-VERIFY" to xVerify,
        "X-MERCHANT-ID" to Constants.MERCHANT_ID
    )
    viewModel.checkPaymentStatus(headers)
}

fun sha256(input: String): String {
    val bytes = input.toByteArray(Charsets.UTF_8)
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun generateTransactionId(): String {
    return UUID.randomUUID().toString()
}
