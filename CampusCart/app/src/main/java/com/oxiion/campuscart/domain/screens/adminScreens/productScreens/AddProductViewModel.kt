package com.oxiion.campuscart.domain.screens.adminScreens.productScreens

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddProductViewModel:ViewModel() {
    private val storageRef=FirebaseStorage.getInstance().reference
     private val _uploadedImageUrl=MutableStateFlow<String?>(null)
    val uploadedImageUrl:StateFlow<String?> get()=_uploadedImageUrl

    fun uploadImageToFirebase(uri: Uri){
        viewModelScope.launch {
            val fileName="products/${UUID.randomUUID()}.jpg"
            val imageRef=storageRef.child(fileName)

            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri->
                        _uploadedImageUrl.value=downloadUri.toString()
                        Log.d("Firebase","Image Uploaded Successfully")
                    }
                }
                .addOnFailureListener{exception ->
                    Log.d("Firebase","Image Upload Failed : ${exception.message}")
                }
        }
    }
}