package com.example.imhere

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.imhere.pages.HomePage
import com.example.imhere.pages.ReportPage

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Report", Icons.Default.Build)
    )

    var selectedIdx by remember {
        mutableIntStateOf(0)
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed{index, navItem ->  NavigationBarItem(
                    selected = selectedIdx == index,
                    onClick = {
                        selectedIdx = index
                    },
                    icon = {
                        Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                    },
                    label = {
                        Text(text = navItem.label)
                    }
                ) }
            }
        }) { innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding), selectedIdx)
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedNavIdx : Int) {
    when(selectedNavIdx) {
        0 -> HomePage()
        1 -> ReportPage()
    }
}