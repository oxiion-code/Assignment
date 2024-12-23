package com.oxiion.campuscart.domain.models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.domain.repository.ProductRepository
import com.oxiion.campuscart.utils.StateData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    private val storageRef = FirebaseStorage.getInstance().reference
    private val _addProductState = MutableStateFlow<StateData>(StateData.Idle)
    val addProductState: StateFlow<StateData> = _addProductState
    private val _updateProductState = MutableStateFlow<StateData>(StateData.Idle)
    val updateProductState: StateFlow<StateData> = _updateProductState
    private val _deleteProductState = MutableStateFlow<StateData>(StateData.Idle)
    val deleteProductState: StateFlow<StateData> = _deleteProductState



    fun addProductWithImage(
        uri: Uri,
        product: Product,
        authViewModel: AuthViewModel
    ) {
        _addProductState.value = StateData.Loading
        viewModelScope.launch {
            val fileName = "products/${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child(fileName)

            //uploading image to Firebase Storage
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        //update product image url and save to Firebase Storage
                        val updatedProduct = product.copy(image = downloadUri.toString())
                        saveProduct(updatedProduct,authViewModel)
                    }
                }
                .addOnFailureListener { exception ->
                    _addProductState.value =
                        StateData.Error("Image upload failed:${exception.message} ")
                }
        }
    }
    private fun saveProduct(
        product: Product,
        authViewModel: AuthViewModel){
        viewModelScope.launch {
            val result=repository.addProduct(product)
            if (result.isSuccess){
                _addProductState.value=StateData.Success
                Log.i("ProductAdd", "Product added successfully")
                authViewModel.fetchAdminData(authViewModel.uid) // Refresh admin data
            }else{
                _addProductState.value=StateData.Error(
                    result.exceptionOrNull()?.localizedMessage?:"Unknown error"
                )
                Log.i("ProductAdd","Failed to add product")
            }
        }
    }
    // For updating the product with or without a new image
    fun saveUpdatedProduct(product: Product, imageUri: Uri?, authViewModel: AuthViewModel) {
        viewModelScope.launch {
            _updateProductState.value=StateData.Loading
            // Start uploading the image in parallel with saving the product data to Firestore
            val imageUploadDeferred = async {
                if (imageUri != null) {
                    // Upload the image and get the image URL
                    val fileName = "products/${UUID.randomUUID()}.jpg"
                    val imageRef = storageRef.child(fileName)
                    try {
                        val uploadTask = imageRef.putFile(imageUri)
                        val downloadUri = uploadTask.await().storage.downloadUrl.await()
                        return@async downloadUri.toString() // Return the image URL
                    } catch (e: Exception) {
                        // Handle any error with image upload
                        throw e
                    }
                } else {
                    return@async product.image // If no image is provided, keep the existing image URL
                }
            }

            try {
                // Get the image URL if it's available
                val updatedImageUrl = imageUploadDeferred.await()

                // Update the product with the new image URL
                val updatedProduct = product.copy(
                    image = updatedImageUrl
                )

                // Save the product data to Firestore
                val result = repository.updateProduct(updatedProduct)
                if (result.isSuccess) {
                    _updateProductState.value = StateData.Success
                    authViewModel.fetchAdminData(authViewModel.uid) // Refresh admin data if necessary
                } else {
                    _updateProductState.value = StateData.Error(result.exceptionOrNull()?.localizedMessage ?: "Unknown error")
                }
            } catch (e: Exception) {
                // Handle error during upload or Firestore operation
                _updateProductState.value = StateData.Error("Error: ${e.message}")
            }
        }
    }

    fun deleteProduct(product:Product,authViewModel: AuthViewModel){
        _deleteProductState.value=StateData.Loading
        viewModelScope.launch {
            val result=repository.deleteProduct(product.id)
            if (result.isSuccess){
                _deleteProductState.value=StateData.Success
                Log.i("ProductDeletion", "Product deleted successfully")
                authViewModel.fetchAdminData(authViewModel.uid)
            }else{
                _deleteProductState.value=StateData.Error(
                    result.exceptionOrNull()?.localizedMessage?:"Unknown error"
                )
                Log.i("ProductDeletion","Failed to delete product")
            }
        }
    }

    // Reset Add Product State
    fun resetAddProductState() {
        _addProductState.value = StateData.Idle
    }
    fun resetDeleteProductState(){
        _deleteProductState.value=StateData.Idle
    }

    // Reset Update Product State
    fun resetUpdateProductState() {
        _updateProductState.value = StateData.Idle
    }

}