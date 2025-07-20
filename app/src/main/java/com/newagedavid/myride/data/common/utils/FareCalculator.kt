package com.newagedavid.myride.data.common.utils

import com.newagedavid.myride.data.common.model.FareEstimate
import javax.inject.Inject

class FareCalculator @Inject constructor() {

    private val baseFare = 2.5
    private val perKmRate = 1.0

    fun calculateFare(
        distanceInKm: Double,
        isPeakHour: Boolean,
        trafficMultiplier: Double = 1.0
    ): FareEstimate {
        val distanceFare = distanceInKm * perKmRate
        val demandMultiplier = if (isPeakHour) 1.5 else 1.0
        val totalFare = (baseFare + distanceFare) * demandMultiplier * trafficMultiplier

        return FareEstimate(
            baseFare = baseFare,
            distanceFare = distanceFare,
            demandMultiplier = demandMultiplier,
            totalFare = totalFare,
            distanceInKm
        )
    }
}
