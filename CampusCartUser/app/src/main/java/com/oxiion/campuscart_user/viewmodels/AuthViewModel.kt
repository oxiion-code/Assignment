package com.oxiion.campuscart_user.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.oxiion.campuscart_user.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @ApplicationContext private val context: Context
):ViewModel() {

}