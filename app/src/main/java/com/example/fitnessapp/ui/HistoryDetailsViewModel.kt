package com.example.fitnessapp.ui

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.fitnessapp.R
import com.example.fitnessapp.repository.LocationRepository
import com.example.fitnessapp.repository.model.HistoryDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryDetailsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _historyDetailsFlow = MutableStateFlow(HistoryDetails(emptyList(), emptyList(), emptyList()))
    val historyDetailsFlow: StateFlow<HistoryDetails> = _historyDetailsFlow

    private val _avgSpeedChartData = MutableStateFlow<ChartData?>(null)
    val avgSpeedChartData: StateFlow<ChartData?> = _avgSpeedChartData

    private val _altitudeChartData = MutableStateFlow<ChartData?>(null)
    val altitudeChartData: StateFlow<ChartData?> = _altitudeChartData

    fun getHistoryDetails(activityId: Long) {
        viewModelScope.launch {
            locationRepository.getHistoryDetails(activityId)
                .collect { historyDetails ->
                    _historyDetailsFlow.value = historyDetails
                }
        }
    }

    fun calculateAvgSpeedChartData(
        points: List<Point>,
        context: Context,
        colorPrimary: Color,
        colorInversePrimary: Color,
        lineType: LineType
    ) {
        viewModelScope.launch {
            val chartData = generateChartData(points, context, colorPrimary, colorInversePrimary, lineType)
            _avgSpeedChartData.value = chartData
        }
    }

    fun calculateAltitudeChartData(
        points: List<Point>,
        context: Context,
        colorPrimary: Color,
        colorInversePrimary: Color,
        lineType: LineType
    ) {
        viewModelScope.launch {
            val chartData = generateChartData(points, context, colorPrimary, colorInversePrimary, lineType)
            _altitudeChartData.value = chartData
        }
    }

    private fun generateChartData(
        points: List<Point>,
        context: Context,
        colorPrimary: Color,
        colorInversePrimary: Color,
        lineType: LineType,
    ): ChartData {
        val xAxisSteps = if (points.isNotEmpty()) points.size - 1 else 0

        val xAxisData = AxisData.Builder()
            .axisLineColor(colorPrimary)
            .axisLineThickness(4.dp)
            .axisStepSize(28.dp)
            .backgroundColor(Color.White)
            .steps(xAxisSteps)
            .labelData { index -> index.toString() }
            .build()

        val yMin = points.minOfOrNull { point -> point.y } ?: 1f
        val yMax = points.maxOfOrNull { point -> point.y } ?: 1f

        val steps = 2
        val range = yMax - yMin
        val stepSize = range / steps

        val yAxisData = AxisData.Builder()
            .axisLineColor(colorPrimary)
            .axisLineThickness(4.dp)
            .steps(steps)
            .backgroundColor(Color.White)
            .labelData { step ->
                val stepValue = yMin + step * stepSize
                String.format(context.getString(R.string.step_value_format), stepValue)
            }.build()

        val lineChartData = LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = points,
                        LineStyle(color = colorPrimary, lineType = lineType),
                        IntersectionPoint(radius = 4.dp, color = colorPrimary),
                        SelectionHighlightPoint(radius = 4.dp),
                        ShadowUnderLine(color = colorInversePrimary),
                        selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y ->
                            val xLabel = String.format(context.getString(R.string.x_label), (x - 1).toInt())
                            val yLabel = String.format(context.getString(R.string.y_label), y)
                            "$xLabel $yLabel"
                        })
                    )
                )
            ),
            xAxisData = xAxisData,
            yAxisData = yAxisData,
        )
        return ChartData(points, lineChartData)
    }

    data class ChartData(
        val points: List<Point>,
        val lineChartData: LineChartData
    )
}
