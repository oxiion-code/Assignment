package com.oxiion.campuscart.domain.models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.oxiion.campuscart.data.models.productUtils.Order
import com.oxiion.campuscart.data.models.productUtils.Product
import com.oxiion.campuscart.data.models.roles.CampusMan
import com.oxiion.campuscart.domain.repository.CampusManRepository
import com.oxiion.campuscart.utils.StateData
import com.oxiion.campuscart.utils.generateRandomId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CampusManViewModel @Inject constructor(
    private val repository: CampusManRepository
) : ViewModel() {
    private val storageRef = FirebaseStorage.getInstance().reference

    private val _campusManData = MutableStateFlow(CampusMan())
    val campusManData: StateFlow<CampusMan?> = _campusManData

    private val _memberStockItems = MutableStateFlow<List<Product>>(emptyList())
    val memberStockItems: StateFlow<List<Product>> = _memberStockItems

    private val _campusManId = MutableStateFlow<String?>(null)
    val campusManId: StateFlow<String?> = _campusManId

    private val _addCampusManState = MutableStateFlow<StateData>(StateData.Idle)
    val addCampusManState: StateFlow<StateData> = _addCampusManState

    private val _updateCampusManState = MutableStateFlow<StateData>(StateData.Idle)
    val updateCampusManState: StateFlow<StateData> = _updateCampusManState

    private val _addStockItemState = MutableStateFlow<StateData>(StateData.Idle)
    val addStockItemState: StateFlow<StateData> = _addStockItemState

    private val _updateStockItemState = MutableStateFlow<StateData>(StateData.Idle)
    val updateStockItemState: StateFlow<StateData> = _updateStockItemState

    private val _deleteStockItemReason = MutableStateFlow<StateData.Idle>(StateData.Idle)
    val deleteStockItemReason: StateFlow<StateData.Idle> = _deleteStockItemReason

    private val _deleteStockItemState = MutableStateFlow<StateData>(StateData.Idle)
    val deleteStockItemState: StateFlow<StateData> = _deleteStockItemState

    private val _deleteCampusManState = MutableStateFlow<StateData>(StateData.Idle)
    val deleteCampusManState: StateFlow<StateData> = _deleteCampusManState

    private val _liveOrders = MutableStateFlow<List<Order>>(emptyList())
    val liveOrders: StateFlow<List<Order>> = _liveOrders

    private val _pastOrders = MutableStateFlow<List<Order>>(emptyList())
    val pastOrders: StateFlow<List<Order>> = _liveOrders

    fun addCampusManWithImage(
        uri: Uri,
        campusMan: CampusMan,
        authViewModel: AuthViewModel
    ) {
        _addCampusManState.value = StateData.Loading
        viewModelScope.launch {
            val fileName = "campusmen/${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child(fileName)

            //uploading image to Firebase Storage
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadedUri ->
                        val updatedCampusMan = campusMan.copy(imageUrl = downloadedUri.toString())
                        saveCampusMan(updatedCampusMan, authViewModel)
                    }
                }
                .addOnFailureListener { exception ->
                    _addCampusManState.value =
                        StateData.Error("Image upload failed:${exception.message} ")
                }
        }
    }

    private fun saveCampusMan(
        campusMan: CampusMan,
        viewModel: AuthViewModel
    ) {
        viewModelScope.launch {
            val result = repository.addMember(campusMan)
            if (result.isSuccess) {
                _addCampusManState.value = StateData.Success
                Log.i("CampusManAdd", "CampusMan added successfully")
                viewModel.fetchAdminData(viewModel.uid) // Refresh admin data
            } else {
                _addCampusManState.value = StateData.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                )
                Log.i("CampusManAdd", "Failed to add campusman")
            }
        }
    }

    fun saveUpdatedCampusman(campusman: CampusMan, imageUri: Uri?, authViewModel: AuthViewModel) {
        viewModelScope.launch {
            _updateCampusManState.value = StateData.Loading
            // Start uploading the image in parallel with saving the product data to Firestore
            val imageUploadDeferred = async {
                if (imageUri != null) {
                    // Upload the image and get the image URL
                    val fileName = "campusmen/${UUID.randomUUID()}.jpg"
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
                    return@async campusman.imageUrl// If no image is provided, keep the existing image URL
                }
            }
            try {
                // Get the image URL if it's available
                val updatedImageUrl = imageUploadDeferred.await()

                // Update the product with the new image URL
                val updatedMember = campusman.copy(
                    imageUrl = updatedImageUrl
                )
                // Save the product data to Firestore
                val result = repository.updateMember(updatedMember)
                if (result.isSuccess) {
                    _updateCampusManState.value = StateData.Success
                    authViewModel.fetchAdminData(authViewModel.uid) // Refresh admin data if necessary
                } else {
                    _updateCampusManState.value = StateData.Error(
                        result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                    )
                }
            } catch (e: Exception) {
                // Handle error during upload or Firestore operation
                _updateCampusManState.value = StateData.Error("Error: ${e.message}")
            }
        }
    }

    fun deleteCampusman(campusMan: CampusMan, authViewModel: AuthViewModel) {
        _deleteCampusManState.value = StateData.Loading
        viewModelScope.launch {
            val result = repository.deleteMember(campusMan.id)
            if (result.isSuccess) {
                _deleteCampusManState.value = StateData.Success
                Log.i("Member Deletion", "member deleted successfully")
                authViewModel.fetchAdminData(authViewModel.uid)
            } else {
                _deleteCampusManState.value = StateData.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                )
                Log.i("member Deletion", "Failed to delete product")
            }
        }
    }

    fun addProductWithImage(
        uri: Uri,
        product: Product,
        authViewModel: AuthViewModel,
        campusManId: String
    ) {
        _addStockItemState.value = StateData.Loading
        viewModelScope.launch {
            val fileName = "products/${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child(fileName)

            //uploading image to Firebase Storage
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        //update product image url and save to Firebase Storage
                        val updatedProduct = product.copy(image = downloadUri.toString())
                        saveProduct(
                            product = updatedProduct,
                            authViewModel = authViewModel,
                            campusmanId = campusManId
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    _addStockItemState.value =
                        StateData.Error("Image upload failed:${exception.message} ")
                }
        }
    }

    private fun saveProduct(
        product: Product,
        campusmanId: String,
        authViewModel: AuthViewModel
    ) {
        viewModelScope.launch {
            val result =
                repository.addStockItemToCampusMan(campusManId = campusmanId, product = product)
            if (result.isSuccess) {
                _addStockItemState.value = StateData.Success
                Log.i("ProductAdd", "Product added successfully")
                authViewModel.fetchAdminData(authViewModel.uid) // Refresh admin data
            } else {
                _addStockItemState.value = StateData.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                )
                Log.i("ProductAdd", "Failed to add product")
            }
        }
    }

    fun fetchMemberStockItems(campusManId: String) {
        viewModelScope.launch {
            repository.getCampusManById(campusManId)?.let { campusMan ->
                _memberStockItems.value = campusMan.memberStockItems
            }
        }
    }

    fun saveUpdatedProduct(
        campusManId: String,
        product: Product,
        imageUri: Uri?,
        authViewModel: AuthViewModel
    ) {
        viewModelScope.launch {
            _updateStockItemState.value = StateData.Loading
            // Start uploading the image in parallel with updating the product
            val imageUploadDeferred = async {
                if (imageUri != null) {
                    // Upload the image and get the image URL
                    val fileName = "products/${UUID.randomUUID()}.jpg"
                    val imageRef = storageRef.child(fileName)
                    try {
                        val uploadTask = imageRef.putFile(imageUri)
                        val downloadUri = uploadTask.await().storage.downloadUrl.await()
                        return@async downloadUri.toString() // Return the new image URL
                    } catch (e: Exception) {
                        // Handle image upload errors
                        throw e
                    }
                } else {
                    return@async product.image // If no new image is provided, retain the existing URL
                }
            }

            try {
                // Fetch updated image URL
                val updatedImageUrl = imageUploadDeferred.await()

                // Fetch CampusMan data
                val campusMan = repository.getCampusManById(campusManId)
                if (campusMan != null) {
                    // Update the product in the stock items list
                    val updatedStockItems = campusMan.memberStockItems.map { stockItem ->
                        if (stockItem.id == product.id) {
                            product.copy(image = updatedImageUrl) // Update with new image URL
                        } else {
                            stockItem // Keep other products unchanged
                        }
                    }

                    // Save the updated CampusMan back to Firestore
                    val updatedCampusMan = campusMan.copy(memberStockItems = updatedStockItems)
                    val result = repository.updateMember(updatedCampusMan)

                    if (result.isSuccess) {
                        _updateStockItemState.value = StateData.Success
                        authViewModel.fetchAdminData(authViewModel.uid) // Refresh admin data
                    } else {
                        _updateStockItemState.value = StateData.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                        )
                    }
                } else {
                    _updateStockItemState.value = StateData.Error("CampusMan not found")
                }
            } catch (e: Exception) {
                // Handle errors during image upload or Firestore operations
                _updateStockItemState.value = StateData.Error("Error: ${e.message}")
            }
        }
    }
    fun deleteStockItem(
        campusManId: String,
        productId: String,
    ) {
        viewModelScope.launch {
            try {
                _deleteStockItemState.value=StateData.Loading
                val campusMan = repository.getCampusManById(campusManId)
                if (campusMan != null) {
                    val updatedStockItems = campusMan.memberStockItems.filter { it.id != productId }
                    val updatedCampusMan = campusMan.copy(memberStockItems = updatedStockItems)
                    val result = repository.updateMember(updatedCampusMan)
                    if (result.isSuccess) {
                        _deleteStockItemState.value=StateData.Success
                    } else {
                        _deleteStockItemState.value=StateData.Error(
                            result.exceptionOrNull()?.localizedMessage.toString()
                        )
                    }
                } else {
                   _deleteStockItemState.value=StateData.Error("Unable to delete")
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { Log.e("Error in deleting", it) }
            }
        }
    }
    fun generateCampusManId(cause: String, email: String) {
        generateRandomId(
            onSuccess = { id ->
                Log.i("id generated", id)
                id.also { _campusManId.value = it }
            },
            onFailure = {
                _campusManId.value = null
                Log.i("id generation error", it.toString())
            },
            cause = cause,
            email = email
        )
    }

    fun fetchLiveOrders(campusManId: String, authViewModel: AuthViewModel) {
        viewModelScope.launch {
            try {
                val adminId = authViewModel.uid // Get the current user's UID
                val orders = repository.getLiveOrders(adminId!!, campusManId)
                _liveOrders.value = orders
            } catch (e: Exception) {
                _liveOrders.value = emptyList()
                Log.e("LiveOrdersVM", "Error fetching live orders: ${e.message}")
            }
        }
    }

    fun fetchPastOrders(campusManId: String, authViewModel: AuthViewModel) {
        viewModelScope.launch {
            try {
                val adminId = authViewModel.uid // Get the current user's UID
                val orders = repository.getPastOrders(adminId!!, campusManId)
                _pastOrders.value = orders
            } catch (e: Exception) {
                _pastOrders.value = emptyList()
                Log.e("pastOrdersVM", "Error fetching past orders: ${e.message}")
            }
        }
    }


    fun saveCampusManData(campusMan: CampusMan) {
        _campusManData.value = campusMan
    }

    fun resetUniqueId() {
        _campusManId.value = null
    }

    fun resetAddMemberState() {
        _addCampusManState.value = StateData.Idle
    }

    fun resetCampusManData() {
        _campusManData.value = CampusMan() // Reset to an empty or default object
    }

    fun resetDeleteMemberState() {
        _deleteCampusManState.value = StateData.Idle
    }

    fun resetAddStockItemState() {
        _addStockItemState.value = StateData.Idle
    }
    fun resetDeleteProductState(){
        _deleteStockItemState.value=StateData.Idle
    }
    fun resetProductUpdateState(){
        _updateStockItemState.value=StateData.Idle
    }
    // Reset Update Product State
    fun resetUpdateMemberState() {
        _updateCampusManState.value = StateData.Idle
    }
}