package com.oxiion.campuscart.domain.models

import androidx.lifecycle.ViewModel
import com.oxiion.campuscart.domain.repository.CampusManRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CampusManViewModel @Inject constructor(
    private val repository:CampusManRepository
):ViewModel() {

}