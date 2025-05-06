package com.example.smarthome.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarthome.WindViewModel

@Composable
fun CurrentWindSpeedScreen(viewModel: WindViewModel = viewModel()) {
    val windEntries by viewModel.allEntries.collectAsState(initial = emptyList())
    val currentSpeed = windEntries.lastOrNull()?.speed ?: 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Aktualna prędkość wiatru", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${String.format("%.1f", currentSpeed)} m/s",
            fontSize = 48.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
