package com.oxiion.campuscart.domain.repository

import com.oxiion.campuscart.data.models.productUtils.Product

interface ProductRepository {
    suspend fun addProduct(product: Product):Result<Boolean>
    suspend fun  updateProduct(product: Product):Result<Boolean>
    suspend fun deleteProduct(productId: String):Result<Boolean>
}