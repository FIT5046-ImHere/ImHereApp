package com.example.imhere.pages.enrollment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import com.example.imhere.ui.components.PageHeader

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EnrollmentScreen(
    navController: NavHostController,
    classSessionId: String,
    viewModel: EnrollmentViewModel = hiltViewModel()
) {
    val students by viewModel.students.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val success by viewModel.success.collectAsState()

    LaunchedEffect(classSessionId) {
        viewModel.load(classSessionId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {

        PageHeader(navController, title = "Enroll to Class")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(students) { student ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleSelection(student.uid) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(student.name)
                    Checkbox(
                        checked = selectedIds.contains(student.uid),
                        onCheckedChange = { viewModel.toggleSelection(student.uid) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.enroll(classSessionId) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Enrolling..." else "Enroll Students")
        }

        if (success != null) {
            Text("Enrolled ${success!!.size} students", color = MaterialTheme.colorScheme.primary)
        }
    }
}
