package com.newagedavid.myride

import com.newagedavid.myride.data.common.utils.FareCalculator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FareCalculatorTest {

    private lateinit var fareCalculator: FareCalculator

    @Before
    fun setUp() {
        fareCalculator = FareCalculator()
    }

    @Test
    fun testBasicFareCalculation() {
        val result = fareCalculator.calculateFare(distanceInKm = 5.0, isPeakHour = false)

        assertEquals(2.5, result.baseFare, 0.01)
        assertEquals(5.0, result.distanceFare, 0.01)
        assertEquals(1.0, result.demandMultiplier, 0.01)
        assertEquals(7.5, result.totalFare, 0.01)
    }

    @Test
    fun testSurgePricing() {
        val result = fareCalculator.calculateFare(distanceInKm = 8.0, isPeakHour = true)

        assertEquals(2.5, result.baseFare, 0.01)
        assertEquals(8.0, result.distanceFare, 0.01)
        assertEquals(1.5, result.demandMultiplier, 0.01)
        assertEquals(15.75, result.totalFare, 0.01)
    }

    @Test
    fun testTrafficMultiplier() {
        val result = fareCalculator.calculateFare(
            distanceInKm = 6.0,
            isPeakHour = false,
            trafficMultiplier = 1.3
        )

        assertEquals(2.5, result.baseFare, 0.01)
        assertEquals(6.0, result.distanceFare, 0.01)
        assertEquals(1.0, result.demandMultiplier, 0.01)
        assertEquals(11.05, result.totalFare, 0.01)
    }
}

