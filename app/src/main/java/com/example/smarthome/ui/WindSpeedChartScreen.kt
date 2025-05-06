package com.example.smarthome.ui

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarthome.NoteViewModel
import com.example.smarthome.WindViewModel
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.coroutineContext


@Composable
fun WindChartScreen(viewModel: WindViewModel = viewModel()) {
    val windEntries = viewModel.allEntries.collectAsState(
        initial = emptyList()
    ).value

    val entries = windEntries.mapIndexed { index, entry ->
        Entry(index.toFloat(), entry.speed)
    }

    AndroidView(
        factory = { ctx ->
            LineChart(ctx).apply {
                description.text = "Prędkość wiatru w czasie"
                description.textColor = Color.DKGRAY
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(false)
                axisRight.isEnabled = false
                legend.isEnabled = false

                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index >= 0 && index < windEntries.size) {
                            val date = SimpleDateFormat("HH:mm", Locale.getDefault())
                                .format(Date(windEntries[index].timestamp))
                            date
                        } else ""
                    }
                }

                val dataSet = LineDataSet(entries, "Prędkość wiatru (m/s)").apply {
                    color = Color.BLUE
                    valueTextColor = Color.BLACK
                    setCircleColor(Color.RED)
                    setDrawFilled(true)
                    fillColor = Color.CYAN
                }

                data = LineData(dataSet)
                invalidate()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

/*
@Composable
fun MoodChartScreen(viewModel: MoodViewModel = viewModel()) {
    val context = LocalContext.current
    val moodEntries = viewModel.allEntries.value ?: emptyList()

    val entries = moodEntries.mapIndexed { index, entry ->
        Entry(index.toFloat(), entry.mood.toFloat())
        //Entry(index.toFloat(), entry.mood.toFloat())
    }

    AndroidView(
        factory = { ctx ->
            LineChart(ctx).apply {
                description.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(false)
                axisRight.isEnabled = false
                legend.isEnabled = false

                val dataSet = LineDataSet(entries, "Produktywność w czasie").apply {
                    color = Color.BLUE
                    valueTextColor = Color.BLACK
                    setCircleColor(Color.RED)
                    setDrawFilled(true)
                    fillColor = Color.LTGRAY
                }

                data = LineData(dataSet)
                invalidate()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
*/