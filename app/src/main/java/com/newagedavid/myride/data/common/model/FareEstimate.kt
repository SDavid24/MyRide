package com.newagedavid.myride.data.common.model

data class FareEstimate(
    val baseFare: Double,
    val distanceFare: Double,
    val demandMultiplier: Double,
    val totalFare: Double,
    val distanceInKm: Double
){
    val roundedTotalFare: Double
        get() = String.format("%.2f", totalFare).toDouble()

}
