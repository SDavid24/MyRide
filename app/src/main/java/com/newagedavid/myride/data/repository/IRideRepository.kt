package com.newagedavid.myride.data.repository

import com.google.android.gms.maps.model.LatLng
import com.newagedavid.myride.data.common.model.Driver
import com.newagedavid.myride.data.common.model.FareEstimate
import com.newagedavid.myride.data.common.model.RideConfirmation

interface IRideRepository {

    fun estimateFare(
        distanceInKm: Double,
        isPeakHour: Boolean,
        trafficMultiplier: Double
    ): FareEstimate

    fun requestRide(): RideConfirmation

    suspend fun saveRideToHistory(
        pickup: LatLng,
        destination: LatLng,
        fare: Double,
        driver: Driver
    )
}
