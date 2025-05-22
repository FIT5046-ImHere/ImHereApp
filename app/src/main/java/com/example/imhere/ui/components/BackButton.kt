package com.example.imhere.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun BackButton(navController: NavHostController, onBack: (() -> Unit)? = null) {
    IconButton(onClick = {
        onBack?.invoke() ?: navController.popBackStack()
    }) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
}