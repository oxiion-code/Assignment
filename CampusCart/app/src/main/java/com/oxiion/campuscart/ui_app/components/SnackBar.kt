package com.oxiion.campuscart.ui_app.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun appSnackBar(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    message: String,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration
        )
    }
}
