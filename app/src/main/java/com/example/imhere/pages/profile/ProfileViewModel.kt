package com.example.imhere.pages.profile
import UserProfileEntity
import android.util.Log

import androidx.compose.runtime.collectAsState
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
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {
    var profile by mutableStateOf<UserProfile?>(null)
    var dbProfile by mutableStateOf<UserProfileEntity?>(null)


    init {
        viewModelScope.launch {
            accountService.currentUserProfile.collect {
                profile = it
                Log.d("ProfileViewModel", "Firebase Profile: $it")
            }
        }

        viewModelScope.launch {
            accountService.dbUserProfile.collect {
                val currUserEnt = it[0]
                Log.d("CURRUSER", currUserEnt.toString())
                currUserEnt.let {
                    dbProfile = currUserEnt
                }
            }
        }
    }


    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                accountService.signOut()
                onSuccess()
            } finally {

            }
        }
    }
}