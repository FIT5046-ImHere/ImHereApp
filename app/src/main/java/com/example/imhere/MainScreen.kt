package com.example.imhere

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.imhere.pages.HomePage
import com.example.imhere.pages.ReportPage

data class NavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Schedules", Icons.Default.DateRange),
        NavItem("Report", Icons.Default.Build),
        NavItem("Profile", Icons.Default.Person)
    )

    var selectedIdx by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIdx == index,
                        onClick = { selectedIdx = index },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                        },
                        label = { Text(text = navItem.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIdx = selectedIdx
        )
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIdx: Int) {
    when (selectedIdx) {
        0 -> HomePage()
        1 -> SchedulesPage(modifier)
        2 -> ReportPage(modifier)
        3 -> ProfilePage(modifier)
    }
}

@Composable
fun SchedulesPage(modifier: Modifier = Modifier) {
    Text(
        text = "Schedules Page (Under Construction)",
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    Text(
        text = "Profile Page (Under Construction)",
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        style = MaterialTheme.typography.titleMedium
    )
}