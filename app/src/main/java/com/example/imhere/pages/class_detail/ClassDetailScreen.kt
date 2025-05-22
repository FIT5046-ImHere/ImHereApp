package com.example.imhere.pages.class_detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imhere.model.ClassSession
import com.example.imhere.model.ClassSessionRecurrence
import com.example.imhere.model.UserProfileType
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClassDetailScreen(
    navController: NavHostController,
    viewModel: ClassDetailViewModel = hiltViewModel(),
    classSessionId: String
) {
    val profile = viewModel.profile
    val userType = profile?.type

    when (userType) {
        null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        UserProfileType.STUDENT -> {
            val sampleClass = ClassSession(
                id = "class001",
                name = "Mathematics 101",
                location = "Room A-101",
                unitCode = "FIT5046",
                teacherId = "teacher001",
                recurrence = ClassSessionRecurrence.WEEKLY,
                startDateTime = Date(),
                endDateTime = Date()
            )
            StudentClassDetailPage(
                navController = navController,
                classInfo = sampleClass,
                classSessionId = classSessionId
            )
        }

        UserProfileType.TEACHER -> {
            TeacherClassDetailPage(
                navController = navController,
                className = "FIT5046 - Mobile Computing",
                startTime = "09:00 AM",
                endTime = "10:00 AM",
                classSessionId = classSessionId
            )
        }
    }
}