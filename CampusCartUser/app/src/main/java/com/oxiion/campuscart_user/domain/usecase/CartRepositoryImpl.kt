package com.oxiion.campuscart_user.domain.usecase

import com.oxiion.campuscart_user.data.datasource.local.CartDao
import com.oxiion.campuscart_user.data.datasource.local.CartItem
import com.oxiion.campuscart_user.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) {

    suspend fun addToCart(product: Product, quantity: Int) {
        withContext(Dispatchers.IO) {
            if (quantity <= 0) return@withContext // Prevent invalid quantities
            val existingItem = cartDao.findCartItemByProductId(product.id)
            if (existingItem != null) {
                val updatedItem = existingItem.copy(
                    quantity = existingItem.quantity + quantity,
                    totalPrice = (existingItem.quantity + quantity) * existingItem.price
                )
                cartDao.updateCartItem(updatedItem)
            } else {
                val newItem = CartItem(
                    productId = product.id,
                    productName = product.name,
                    productCategory = product.category,
                    quantity = quantity,
                    rating = product.rating,
                    isAvailable = product.isAvailable,
                    discountedPrice = product.discount,
                    description = product.description,
                    price = product.price,
                    image = product.image,
                    totalPrice = quantity * product.price
                )
                cartDao.addToCart(newItem)
            }
        }
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        withContext(Dispatchers.IO) {
            if (cartItem.quantity <= 0) {
                cartDao.removeFromCart(cartItem) // Automatically remove items with zero or negative quantity
            } else {
                cartDao.updateCartItem(cartItem)
            }
        }
    }


    suspend fun removeFromCart(cartItem: CartItem) {
        withContext(Dispatchers.IO) {
            cartDao.removeFromCart(cartItem)
        }
    }

    suspend fun findCartItemByProductId(productId: String): CartItem? {
        return withContext(Dispatchers.IO) {
            cartDao.findCartItemByProductId(productId)
        }
    }

    suspend fun getAllCartItems(): List<CartItem> {
        return withContext(Dispatchers.IO) {
            cartDao.getAllCartItems()
        }
    }

    suspend fun getTotalPrice(): Double {
        return withContext(Dispatchers.IO) {
            cartDao.getTotalPrice() // Return 0.0 if no items are in the cart
        }
    }

    suspend fun getDiscountedPrice(): Double {
        return withContext(Dispatchers.IO) {
            cartDao.getTotalDiscountedPrice() // Return 0.0 if no items are in the cart
        }
    }

    suspend fun clearCart() {
        withContext(Dispatchers.IO) {
            cartDao.clearCart()
        }
    }
}
