package com.example.imhere.pages.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.imhere.ui.theme.ImHereTheme

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val errorMessage = viewModel.errorMessage;

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    fun onLogin() {
        viewModel.login(email, password) {
            Log.d("LoginViewModel", "Login successful for $email")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    val icon = if (passwordVisible) Icons.Default. else Icons.Filled.VisibilityOff
//                    Icon(
//                        imageVector = icon,
//                        contentDescription = null,
//                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
//                    )
//                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onLogin() },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable { /* no action yet */ }
                        .padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Don't have an account? Sign up",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable { /* no action yet */ }
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ImHereTheme {
        LoginScreen()
    }
}