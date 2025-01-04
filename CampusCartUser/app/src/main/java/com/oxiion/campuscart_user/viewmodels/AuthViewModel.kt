package com.oxiion.campuscart_user.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oxiion.campuscart_user.domain.repository.AuthRepository
import com.oxiion.campuscart_user.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _getCollegeListState = MutableStateFlow<DataState>(DataState.Idle)
    val getCollegeListState: StateFlow<DataState> = _getCollegeListState

    private val _getHostelListState = MutableStateFlow<DataState>(DataState.Idle)
    val getHostelListState: StateFlow<DataState> = _getHostelListState

    val collegeList = MutableStateFlow<List<String>>(listOf())
    val hostelList = MutableStateFlow<List<String>>(listOf())

    fun getCollegeList() {
        if (_getCollegeListState.value is DataState.Success && collegeList.value.isNotEmpty()) return

        _getCollegeListState.value = DataState.Loading
        viewModelScope.launch {
            val result = repository.getCollageList()
            if (result.isSuccess) {
                _getCollegeListState.value = DataState.Success
                collegeList.value = result.getOrNull() ?: listOf()
            } else {
                _getCollegeListState.value = DataState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun getHostelListIfNeeded(collegeName: String) {
        if (_getHostelListState.value is DataState.Success && hostelList.value.isNotEmpty()) return

        _getHostelListState.value = DataState.Loading
        viewModelScope.launch {
            val result = repository.getHostelList(collegeName)
            if (result.isSuccess) {
                _getHostelListState.value = DataState.Success
                hostelList.value = result.getOrNull() ?: listOf()
            } else {
                _getHostelListState.value = DataState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }
}
