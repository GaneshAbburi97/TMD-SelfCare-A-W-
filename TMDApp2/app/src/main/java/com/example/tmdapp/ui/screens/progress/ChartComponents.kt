package com.example.tmdapp.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun SmoothLineChartSection(
    title: String,
    entries: List<Entry>,
    labels: List<String>,
    lineColor: Int,
    fillColor: Int? = null
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f).toArgb()
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f).toArgb()
    val circleHoleColorVal = MaterialTheme.colorScheme.surface.toArgb()

    Column {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (entries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Not enough data to display chart", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 14.sp)
                }
            } else {
                AndroidView(
                    factory = { ctx ->
                        LineChart(ctx).apply {
                            description.isEnabled = false
                            legend.isEnabled = false
                            setDrawGridBackground(false)
                            setDrawBorders(false)
                            setScaleEnabled(false)
                            setPinchZoom(false)
                            setDoubleTapToZoomEnabled(false)

                            // Interaction
                            setTouchEnabled(true)
                            isDragEnabled = true

                            // Axis
                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                setDrawGridLines(false)
                                granularity = 1f
                                textColor = labelColor
                                textSize = 10f
                                yOffset = 10f
                                axisLineColor = android.graphics.Color.TRANSPARENT
                            }
                            axisLeft.apply {
                                setDrawGridLines(true)
                                this.gridColor = gridColor
                                textColor = labelColor
                                axisMinimum = 0f
                                setLabelCount(5, true)
                                axisLineColor = android.graphics.Color.TRANSPARENT
                            }
                            axisRight.isEnabled = false

                            animateX(1000)
                        }
                    },
                    update = { chart ->
                        val dataSet = LineDataSet(entries, title).apply {
                            color = lineColor
                            setDrawValues(false) // Hide values on nodes for a cleaner look
                            setDrawCircles(true)
                            setCircleColor(lineColor)
                            circleRadius = 4f
                            circleHoleColor = circleHoleColorVal
                            lineWidth = 3f
                            mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth line

                            if (fillColor != null) {
                                setDrawFilled(true)
                                this.fillColor = fillColor
                                fillAlpha = 50
                            }
                        }

                        chart.data = LineData(dataSet)
                        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

                        // Enable horizontal scrolling if data is large
                        if (entries.size > 7) {
                            chart.setVisibleXRangeMaximum(7f)
                            chart.moveViewToX(entries.size.toFloat())
                        }

                        chart.invalidate()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
    }
}
