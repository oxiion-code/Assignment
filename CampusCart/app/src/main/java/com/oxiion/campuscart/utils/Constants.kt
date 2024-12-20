package com.oxiion.campuscart.utils

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.oxiion.campuscart.domain.models.AdminViewModel
@Composable
fun constants():AdminViewModel{
    val viewModel:AdminViewModel=hiltViewModel()
    return  viewModel
}