package com.example.imhere.pages.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.service.AccountService
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                accountService.authenticate(email, password)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Login failed"
            } finally {
                isLoading = false
            }
        }
    }
}