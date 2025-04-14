package com.example.imhere.pages

//import androidx.compose.foundation.layout.Column
//import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//Creating a data model for Class
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

@Composable
fun HomePage(modifier: Modifier = Modifier) {
     Column(
          modifier = modifier
               .fillMaxSize()
               .padding(16.dp),
          verticalArrangement = Arrangement.Top
     ) {
          Text(
               text = "Welcome to ImHere",
               style = MaterialTheme.typography.titleLarge,
               fontWeight = FontWeight.Bold,
               modifier = Modifier.padding(bottom = 16.dp)
          )
          // Sample class for prototype
          val sampleClass = Class(
               name = "Mathematics 101",
               startDateTime = LocalDateTime.now().plusHours(2),
               endDateTime = LocalDateTime.now().plusHours(3),
               recurrence = Recurrence.WEEKLY,
               location = "Room A-101"
          )
          NextClassCard(classItem = sampleClass)
     }
}

@Composable
fun NextClassCard(classItem: Class, modifier: Modifier = Modifier) {
     Card(
          modifier = modifier
               .fillMaxWidth()
               .padding(vertical = 8.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
     ) {
          Column(
               modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
               verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
               Text(
                    text = classItem.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
               )
               Text(
                    text = "Time: ${
                         classItem.startDateTime.format(
                              DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                         )
                    } - ${
                         classItem.endDateTime.format(
                              DateTimeFormatter.ofPattern("HH:mm")
                         )
                    }",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
               )
               Text(
                    text = "Location: ${classItem.location}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
               )
               Text(
                    text = "Recurrence: ${classItem.recurrence.name.lowercase().replaceFirstChar { it.titlecase() }}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
               )
          }
     }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
     MaterialTheme {
          HomePage()
     }
}