package com.example.fitnessapp.repositoryimpl

import android.location.Location
import com.example.fitnessapp.localdatasource.FitnessLocation
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class FitnessActivityMetricsCalculatorTest {

    private val distanceAndDurationCalculator = mockk<DistanceAndDurationCalculator>()

    private val fitnessActivityMetricsCalculator = FitnessActivityMetricsCalculator(distanceAndDurationCalculator)

    @Test
    fun testCalculateTempoWithValidSpeed() {
        val mockLocation = mockk<Location>()
        every { mockLocation.speed } returns 10.0F

        val expectedTempo = 0.25f
        val actualTempo = fitnessActivityMetricsCalculator.calculateTempo(mockLocation, 10, 4f)

        assertEquals(expectedTempo, actualTempo)
    }

    @Test
    fun testCalculateTempoWithIncorrectExpectedValue() {
        val mockLocation = mockk<Location>()
        every { mockLocation.speed } returns 10.0F

        val expectedTempo = 1.5f
        val actualTempo = fitnessActivityMetricsCalculator.calculateTempo(mockLocation, 10, 4f)

        assertNotEquals(expectedTempo, actualTempo)
    }

    @Test
    fun testCalculateTempoWithZeroConversionFactor() {
        val mockLocation = mockk<Location>()
        every { mockLocation.speed } returns 1F

        val expectedTempo = Float.POSITIVE_INFINITY
        val actualTempo = fitnessActivityMetricsCalculator.calculateTempo(mockLocation, 60, 0f)

        assertEquals(expectedTempo, actualTempo)
    }

    @Test
    fun testCalculateTempoWithDefaultParameters() {
        val mockLocation = mockk<Location>()
        every { mockLocation.speed } returns 10.0F

        val expectedTempo = 60 / (10 * 3.6F)
        val actualTempo = fitnessActivityMetricsCalculator.calculateTempo(mockLocation)

        assertEquals(expectedTempo, actualTempo)
    }

    @Test
    fun testCalculateAverageSpeedWithValidInputs() {
        val duration = 4800.seconds

        val distance = 12.0F

        val expectedAverageSpeed = 9f
        val actualAverageSpeed = fitnessActivityMetricsCalculator.calculateAverageSpeed(distance, duration)

        assertEquals(expectedAverageSpeed, actualAverageSpeed)
    }

    @Test
    fun testCalculateAverageSpeedWithIncorrectExpectedValue() {
        val duration = 3600.seconds

        val distance = 10.0F

        val expectedAverageSpeed = 15f
        val actualAverageSpeed = fitnessActivityMetricsCalculator.calculateAverageSpeed(distance, duration)

        assertNotEquals(expectedAverageSpeed, actualAverageSpeed)
    }

    @Test
    fun testCalculateTotalDistanceAndDuration() {
        val location1 = mockk<FitnessLocation>()
        val location2 = mockk<FitnessLocation>()
        val location3 = mockk<FitnessLocation>()
        val location4 = mockk<FitnessLocation>()
        val location5 = mockk<FitnessLocation>()

        every { location1.time } returns 1000L
        every { location2.time } returns 2000L
        every { location3.time } returns 3000L
        every { location4.time } returns 4000L
        every { location5.time } returns 5000L

        every { distanceAndDurationCalculator.getDistance(any(), any()) } returnsMany listOf(
            0.42502F,
            0.42502F,
            0.42502F,
            0.42502F
        )

        every { distanceAndDurationCalculator.getDuration(any(), any()) } returns 1.toDuration(DurationUnit.SECONDS)

        val expectedTotalDistance = 1.70008F
        val expectedDuration: Duration = (4L).toDuration(DurationUnit.SECONDS)

        val actualMetrics = fitnessActivityMetricsCalculator.calculateDistanceAndDuration(
            listOf(location1, location2, location3, location4, location5)
        )

        assertEquals(expectedDuration, actualMetrics.duration)
        assertEquals(expectedTotalDistance, actualMetrics.distance)
    }

    @Test
    fun testCalculateAverageSpeedAndAltitudePerKmWithVaryingDistancesAndAltitudes() {
        val location1 = mockk<FitnessLocation>()
        val location2 = mockk<FitnessLocation>()
        val location3 = mockk<FitnessLocation>()
        val location4 = mockk<FitnessLocation>()

        every { location1.time } returns 1000L
        every { location1.altitude } returns 100.0

        every { location2.time } returns 2000L
        every { location2.altitude } returns 110.0

        every { location3.time } returns 3000L
        every { location3.altitude } returns 120.0

        every { location4.time } returns 4000L
        every { location4.altitude } returns 130.0

        every { distanceAndDurationCalculator.getDistance(any(), any()) } returnsMany listOf(
            0.5F,
            0.6F,
            0.9F
        )

        every { distanceAndDurationCalculator.getDuration(any(), any()) } returnsMany listOf(
            1.toDuration(DurationUnit.SECONDS),
            1.toDuration(DurationUnit.SECONDS),
            1.toDuration(DurationUnit.SECONDS)
        )

        val expectedAverageSpeedPerKm = listOf(0F, 3600F, 3600F)
        val expectedAltitudes = listOf(100.0, 120.0, 130.0)

        val actualStats = fitnessActivityMetricsCalculator.calculateAverageSpeedAndAltitudePerKm(
            listOf(location1, location2, location3, location4)
        )

        assertEquals(expectedAverageSpeedPerKm, actualStats.averageSpeedPerKm)
        assertEquals(expectedAltitudes, actualStats.altitude)
    }
}
