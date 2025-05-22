package com.example.imhere.pages.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.UserProfile
import com.example.imhere.model.UserProfileType
import com.example.imhere.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun register(
        email: String,
        password: String,
        name: String,
        type: String,
        birthDate: Date,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                accountService.signUp(email, password)
                val uid = accountService.currentUserId

                accountService.createUserProfile(uid, UserProfile(
                    uid,
                    name,
                    email,
                    type = UserProfileType.valueOf(type),
                    birthDate
                ))

                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Login failed"
            } finally {
                isLoading = false
            }
        }
    }
}