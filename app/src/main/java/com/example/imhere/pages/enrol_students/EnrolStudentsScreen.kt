package com.example.imhere.pages.enrol_students

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.imhere.model.UserProfile
import com.example.imhere.model.service.ClassSessionService
import com.example.imhere.model.service.StudentService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrolStudentsPage(
    navController: NavController,
    viewModel: EnrolStudentsViewModel = hiltViewModel()
) {
    val context     = LocalContext.current
    val students    by viewModel.students.collectAsState()
    val selected    by viewModel.selectedUids.collectAsState()
    val submitting  by viewModel.isSubmitting.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.submit(
                        onSuccess = {
                            Toast.makeText(context, "Enrolment successful", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
            ) {
//                Icon(, contentDescription = "Submit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = viewModel.className,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn {
                items(students) { student ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggle(student.uid) }
                            .padding(vertical = 12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(student.name, style = MaterialTheme.typography.bodyLarge)
                            Text("UID: ${student.uid}", style = MaterialTheme.typography.bodySmall)
                        }
                        if (student.uid in selected) {
//                            Icon(Icons.Default.Check, contentDescription = "Selected", modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}