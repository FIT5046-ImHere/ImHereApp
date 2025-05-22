package com.example.imhere.pages.class_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.UserProfile
import com.example.imhere.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassDetailViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {
    var profile by mutableStateOf<UserProfile?>(null)

    init {
        viewModelScope.launch {
            accountService.currentUserProfile.collect {
                profile = it
            }
        }
    }
}