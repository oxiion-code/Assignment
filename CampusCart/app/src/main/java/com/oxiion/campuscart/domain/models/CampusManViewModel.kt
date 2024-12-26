package com.oxiion.campuscart.domain.models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
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
    private val repository:CampusManRepository
):ViewModel() {
    private val storageRef = FirebaseStorage.getInstance().reference

    private val _campusManData=MutableStateFlow<CampusMan>(CampusMan())
    val campusManData:StateFlow<CampusMan?> = _campusManData

    private val _campusManId=MutableStateFlow<String?>(null)
    val campusManId:StateFlow<String?> = _campusManId

    private val _addCampusManState=MutableStateFlow<StateData>(StateData.Idle)
    val addCampusManState:StateFlow<StateData> = _addCampusManState

    private val _updateCampusManState=MutableStateFlow<StateData>(StateData.Idle)
    val updateCampusManState:StateFlow<StateData> = _updateCampusManState

    private val _deleteCampusManState=MutableStateFlow<StateData>(StateData.Idle)
    val deleteCampusManState:StateFlow<StateData> = _deleteCampusManState

    fun addCampusManWithImage(
        uri: Uri,
        campusMan:CampusMan,
        authViewModel: AuthViewModel){
        _addCampusManState.value=StateData.Loading
        viewModelScope.launch {
            val fileName="campusmen/${UUID.randomUUID()}.jpg"
            val imageRef=storageRef.child(fileName)

            //uploading image to Firebase Storage
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadedUri->
                        val updatedCampusMan=campusMan.copy(imageUrl = downloadedUri.toString())
                        saveCampusMan(updatedCampusMan,authViewModel)
                    }
                }
                .addOnFailureListener{exception->
                    _addCampusManState.value=StateData.Error("Image upload failed:${exception.message} ")
                }
        }
    }
    private fun saveCampusMan(
        campusMan:CampusMan,
        viewModel: AuthViewModel
    ){
        viewModelScope.launch {
            val result=repository.addMember(campusMan)
            if (result.isSuccess){
                _addCampusManState.value=StateData.Success
                Log.i("CampusManAdd", "CampusMan added successfully")
                viewModel.fetchAdminData(viewModel.uid) // Refresh admin data
            }else{
                _addCampusManState.value=StateData.Error(
                    result.exceptionOrNull()?.localizedMessage?:"Unknown error"
                )
                Log.i("CampusManAdd", "Failed to add campusman")
            }
        }
    }
    fun saveUpdatedCampusman(campusman: CampusMan, imageUri: Uri?, authViewModel: AuthViewModel) {
        viewModelScope.launch {
            _updateCampusManState.value=StateData.Loading
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
                    _updateCampusManState.value = StateData.Error(result.exceptionOrNull()?.localizedMessage ?: "Unknown error")
                }
            } catch (e: Exception) {
                // Handle error during upload or Firestore operation
                _updateCampusManState.value = StateData.Error("Error: ${e.message}")
            }
        }
    }

    fun deleteCampusman(campusMan:CampusMan, authViewModel: AuthViewModel){
        _deleteCampusManState.value=StateData.Loading
        viewModelScope.launch {
            val result=repository.deleteMember(campusMan.id)
            if (result.isSuccess){
                _deleteCampusManState.value=StateData.Success
                Log.i("Member Deletion", "member deleted successfully")
                authViewModel.fetchAdminData(authViewModel.uid)
            }else{
                _deleteCampusManState.value=StateData.Error(
                    result.exceptionOrNull()?.localizedMessage?:"Unknown error"
                )
                Log.i("member Deletion","Failed to delete product")
            }
        }
    }
    fun generateCampusManId(cause:String,email:String){
        generateRandomId(
            onSuccess = {id->
                Log.i("id generated",id)
                id.also { _campusManId.value = it }
            },
            onFailure = {
                _campusManId.value= null
                Log.i("id generation error",it.toString())
            },
            cause=cause,
            email=email
        )
    }
    fun saveCampusManData(campusMan:CampusMan){
        _campusManData.value=campusMan
    }
    fun resetUniqueId(){
        _campusManId.value=null
    }
    fun resetAddMemberState() {
        _addCampusManState.value = StateData.Idle
    }
    fun resetCampusManData() {
        _campusManData.value = CampusMan() // Reset to an empty or default object
    }
    fun resetDeleteMemberState(){
        _deleteCampusManState.value=StateData.Idle
    }

    // Reset Update Product State
    fun resetUpdateMemberState() {
        _updateCampusManState.value = StateData.Idle
    }
}