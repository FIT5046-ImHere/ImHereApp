package com.example.imhere.pages

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d -> birthDate = "$d/${m + 1}/$y" },
        year, month, day
    )
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

    val showToast = { msg: String ->
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    // Column layout for the Registration screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    top = 40.dp,
                    bottom = 16.dp
                ) // Adds padding to move the title down
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { /* Handle back action */ }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            // Name input field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Email input field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Password input field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )


            // Confirm password input field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Your Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )

            Box {
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = {},
                    label = { Text("Birth Date (dd/MM/yyyy)") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { datePickerDialog.show() }
                )
            }


            // Register button
            Button(
                onClick = {
                    when {
                        name.isEmpty() -> showToast("Name is required")
                        email.isEmpty() -> showToast("Email is required")
                        password != confirmPassword -> showToast("Passwords don't match")
                        password.length < 6 || password.length > 12 -> showToast("Password must be 6–12 characters")
                        !password.any { it.isDigit() } -> showToast("Password must contain at least one number")
                        !password.all { it.isLetterOrDigit() } -> showToast("Password must not contain special characters")
                        isFutureDate(birthDate) -> showToast("Birth date cannot be in the future!")
                        else -> showToast("Registration successful!")
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Register")

            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Have an account? Log in",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { /* no action yet */ }
                )
            }

        }
    }
}

// Function to check if the entered birth date is in the future
fun isFutureDate(birthDate: String): Boolean {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currentDate = Calendar.getInstance().time
    val parsedDate = try {
        dateFormat.parse(birthDate)
    } catch (e: Exception) {
        null
    }

    return parsedDate?.after(currentDate) == true
}

@Preview(showBackground = true)
@Composable
fun PreviewRegistration() {
    RegistrationScreen()
}
