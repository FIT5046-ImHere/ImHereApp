package com.example.imhere.pages.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.imhere.R
import com.example.imhere.model.service.AccountService
import com.example.imhere.pages.classes.ClassCard
import com.example.imhere.pages.classes.ClassesViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

data class Class(
    val name: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val recurrence: Recurrence,
    val location: String
)

enum class Recurrence {
     ONCE, DAILY, WEEKLY, MONTHLY
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(
     modifier: Modifier = Modifier,
     navController: NavHostController,
     viewModel: ClassesViewModel = hiltViewModel()
) {
     LazyColumn(
          modifier = modifier
               .fillMaxSize()
               .padding(horizontal = 16.dp),
          verticalArrangement = Arrangement.Top,
          horizontalAlignment = Alignment.CenterHorizontally
     ) {
          item {
               Image(
                    painter = painterResource(id = R.drawable.imhere_logo),
                    contentDescription = "I'm Here App Logo",
                    modifier = Modifier
                         .size(150.dp)
                         .padding(top = 16.dp, bottom = 16.dp) // Padding around logo
               )
          }
          item {
               Text(
                    text = "Welcome ${viewModel.profile?.name}!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
               )
          }
          item {
               Text(
                    text = "Here is your upcoming classes:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
               )
          }
          val now = Date()
          val upcoming = viewModel.classSessions
               .filter { it.startDateTime.after(now) }
               .sortedBy { it.startDateTime }
               .take(2)

          items(upcoming) { classItem ->
               ClassCard(
                    classItem = classItem,
                    onClick = {
                         navController.navigate("classes/${classItem.id}")
                    }
               )
          }
     }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
     val navController = rememberNavController()
     MaterialTheme {
          HomePage(
              navController = navController
          )
     }
}