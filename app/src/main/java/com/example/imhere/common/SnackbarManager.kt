package com.example.imhere.common

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SnackbarManager {
    private val messages: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val snackbarMessages: StateFlow<SnackbarMessage?>
        get() = messages.asStateFlow()

    fun showMessage(@StringRes messageRes: Int) {
        messages.value = SnackbarMessage.ResourceSnackbar(messageRes)
    }

    fun showMessage(message: String) {
        messages.value = SnackbarMessage.StringSnackbar(message)
    }

    fun showMessage(message: SnackbarMessage) {
        messages.value = message
    }

    fun clearSnackbarState() {
        messages.value = null
    }
}
