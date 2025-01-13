package com.oxiion.campuscart_user.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oxiion.campuscart_user.data.datasource.local.CartItem
import com.oxiion.campuscart_user.data.model.Product
import com.oxiion.campuscart_user.domain.usecase.CartRepositoryImpl
import com.oxiion.campuscart_user.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepositoryImpl // Replace with actual repository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    private val _addToCartState = MutableStateFlow<DataState>(DataState.Idle)
    val addToCartState: StateFlow<DataState> = _addToCartState

    private val _discountedPrice=MutableStateFlow(0.0)
    val discountedPrice: StateFlow<Double> = _discountedPrice

    // Add item to the cart
    fun addToCart(product: Product, quantity: Int) {
        _addToCartState.value = DataState.Loading
        viewModelScope.launch {
            try {
                repository.addToCart(product, quantity)
                _addToCartState.value = DataState.Success
                loadCartItems()  // Refresh cart and total after adding an item
            } catch (e: Exception) {
                _addToCartState.value = DataState.Error(e.message ?: "An error occurred")
            }
        }
    }
    fun resetAddToCartState() {
        _addToCartState.value = DataState.Idle
    }
    // Update an existing item in the cart
    fun updateCartItem(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                repository.updateCartItem(cartItem)
                loadCartItems()  // Reload cart items after update
            } catch (e: Exception) {
                // Handle update error if needed
            }
        }
    }

    // Remove item from the cart
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            try {
                repository.removeFromCart(cartItem)
                loadCartItems()  // Reload cart after removal
            } catch (e: Exception) {
                // Handle removal error if needed
            }
        }
    }

    // Find a cart item by its product ID
    fun findCartItemByProductId(productId: String, onResult: (CartItem?) -> Unit) {
        viewModelScope.launch {
            val cartItem = repository.findCartItemByProductId(productId)
            onResult(cartItem)
        }
    }

    // Load all cart items and total price
     fun loadCartItems() {
        viewModelScope.launch {
            _cartItems.value = repository.getAllCartItems()
            _totalPrice.value = repository.getTotalPrice()
            _discountedPrice.value=repository.getDiscountedPrice()
        }
    }

    // Refresh total price manually
    fun refreshTotalPrice() {
        viewModelScope.launch {
            _totalPrice.value = repository.getTotalPrice()
        }
    }

    fun refreshDisCountedPrice() {
        viewModelScope.launch {
            _discountedPrice.value = repository.getDiscountedPrice()
        }
    }
    // Clear the entire cart
    fun clearCart() {
        viewModelScope.launch {
            try {
                repository.clearCart()
                _cartItems.value = emptyList()  // Empty the cart items
                _totalPrice.value = 0.0       // Reset total price
            } catch (e: Exception) {
                // Handle clear cart error if needed
            }
        }
    }
}
