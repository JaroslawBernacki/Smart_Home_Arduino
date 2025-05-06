package com.example.smarthome

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smarthome.R

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_mood), contentDescription = "Mood") },
            label = { Text("Lista") },
            selected = currentRoute == "mood_entry",
            onClick = { navController.navigate("mood_entry") }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_calendar), contentDescription = "Calendar") },
            label = { Text("Kalendarz") },
            selected = currentRoute == "calendar_view",
            onClick = { navController.navigate("calendar_view") }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_graph), contentDescription = "Chart") },
            label = { Text("Wykres") },
            selected = currentRoute == "chart_view",
            onClick = { navController.navigate("chart_view") }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.ic_weather), contentDescription = "Weather") },
            label = { Text("Pogoda") },
            selected = currentRoute == "weather_view",
            onClick = { navController.navigate("weather_view") }
        )
    }
}
