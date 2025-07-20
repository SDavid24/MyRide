package com.newagedavid.myride.repository

import com.google.android.gms.maps.model.LatLng
import com.newagedavid.myride.data.common.model.Driver
import com.newagedavid.myride.data.common.model.FareEstimate
import com.newagedavid.myride.data.common.model.RideConfirmation
import com.newagedavid.myride.data.local.entity.RideHistory
import com.newagedavid.myride.data.repository.IRideRepository

class FakeRideRepository : IRideRepository {

    var savedRides = mutableListOf<RideHistory>()
    var lastRequest: RideConfirmation? = null

    override fun estimateFare(
        distanceInKm: Double,
        isPeakHour: Boolean,
        trafficMultiplier: Double
    ): FareEstimate {
        return FareEstimate(
            baseFare = 2.5,
            distanceFare = distanceInKm,
            demandMultiplier = if (isPeakHour) 1.5 else 1.0,
            totalFare = 10.0,
            distanceInKm = distanceInKm
        )
    }

    override fun requestRide(): RideConfirmation {
        val confirmation = RideConfirmation(
            status = "confirmed",
            driver = Driver("Fake Driver", "Car", "ABC123"),
            estimatedArrival = "5 min"
        )
        lastRequest = confirmation
        return confirmation
    }

    override suspend fun saveRideToHistory(
        pickup: LatLng,
        destination: LatLng,
        fare: Double,
        driver: Driver
    ) {
        savedRides.add(
            RideHistory(
                pickupLat = pickup.latitude,
                pickupLng = pickup.longitude,
                destinationLat = destination.latitude,
                destinationLng = destination.longitude,
                fare = fare,
                timestamp = System.currentTimeMillis(),
                driverName = driver.name,
                car = driver.car,
                plateNumber = driver.plateNumber
            )
        )
    }
}
