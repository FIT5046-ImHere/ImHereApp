package com.example.imhere.pages.classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.ClassSession
import com.example.imhere.model.UserProfile
import com.example.imhere.model.service.AccountService
import com.example.imhere.model.service.ClassSessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassesViewModel @Inject constructor(
    private val classSessionService: ClassSessionService,
    private val accountService: AccountService
) : ViewModel() {

    var classSessions by mutableStateOf<List<ClassSession>>(emptyList())
    var profile by mutableStateOf<UserProfile?>(null)
    var isLoading by mutableStateOf(true)

    init {
        viewModelScope.launch {
            accountService.currentUserProfile.collect { user ->
                profile = user
                loadClassesForUser(user)
            }
        }
    }

    private fun loadClassesForUser(user: UserProfile?) {
        if (user == null) {
            isLoading = false
            return
        }

        viewModelScope.launch {
            try {
                val fetched = when (user.type.name.lowercase()) {
                    "student" -> classSessionService.getAllClassSessions(studentId = user.uid)
                    "teacher" -> classSessionService.getAllClassSessions(teacherId = user.uid)
                    else -> emptyList()
                }
                classSessions = fetched.sortedBy { it.startDateTime }
            } finally {
                isLoading = false
            }
        }
    }
}
