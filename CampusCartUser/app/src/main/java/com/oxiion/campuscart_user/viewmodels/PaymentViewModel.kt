package com.oxiion.campuscart_user.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oxiion.campuscart_user.Constants
import com.oxiion.campuscart_user.api.ApiUtilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class PaymentViewModel : ViewModel() {
    private val _paymentStatus = MutableStateFlow(false)  // Tracks backend response for payment success
    val paymentStatus: StateFlow<Boolean> = _paymentStatus

    private val _paymentVerificationStatus = MutableStateFlow<Boolean?>(null)  // Tracks payment verification result
    val paymentVerificationStatus: StateFlow<Boolean?> = _paymentVerificationStatus

    private var statusChecked = false

    fun checkPaymentStatus(headers: Map<String, String>) {
        viewModelScope.launch {
            try {
                val response = ApiUtilities.statusApi.getPaymentStatus(
                    headers,
                    Constants.MERCHANT_ID,
                    Constants.merchantTransactionId
                )
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    if (responseBody.success) {  // Check additional fields
                        _paymentStatus.value = true
                        _paymentVerificationStatus.value = true
                    } else {
                        Log.e("PaymentStatus", "Transaction not successful: ${responseBody.message}")
                        _paymentStatus.value = false
                        _paymentVerificationStatus.value = false
                    }
                } else {
                    Log.e("PaymentStatus", "Invalid response: ${response.errorBody()?.string()}")
                    _paymentStatus.value = false
                    _paymentVerificationStatus.value = false
                }
            } catch (e: Exception) {
                Log.e("PaymentStatus", "Failed to check payment status: ${e.message}")
                e.printStackTrace()
                _paymentVerificationStatus.value = false
            }
        }
    }


    fun resetPaymentStatus() {
        _paymentStatus.value = false
        _paymentVerificationStatus.value = null
        statusChecked = false
    }

    fun isStatusChecked(): Boolean = statusChecked
}

