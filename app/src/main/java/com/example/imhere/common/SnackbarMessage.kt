package com.example.imhere.common

import android.content.res.Resources
import androidx.annotation.StringRes

sealed class SnackbarMessage {
    data class StringSnackbar(val message: String) : SnackbarMessage()

    data class ResourceSnackbar(
        @StringRes val messageRes: Int? = null,
        val fallbackMessage: String? = null
    ) : SnackbarMessage()

    companion object {
        fun SnackbarMessage.toMessage(resources: Resources): String {
            return when (this) {
                is StringSnackbar -> message
                is ResourceSnackbar -> {
                    messageRes?.let { resources.getString(it) } ?: fallbackMessage.orEmpty()
                }
            }
        }

        fun Throwable.toSnackbarMessage(): SnackbarMessage {
            val message = this.message.orEmpty()
            return if (message.isNotBlank()) StringSnackbar(message)
            else ResourceSnackbar(fallbackMessage = "Oops")
        }
    }
}
