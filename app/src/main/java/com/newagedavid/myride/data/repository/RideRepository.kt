package com.newagedavid.myride.data.repository

import com.google.android.gms.maps.model.LatLng
import com.newagedavid.myride.data.common.model.Driver
import com.newagedavid.myride.data.common.model.FareEstimate
import com.newagedavid.myride.data.common.model.RideConfirmation
import com.newagedavid.myride.data.common.utils.FareCalculator
import com.newagedavid.myride.data.local.dao.RideHistoryDao
import com.newagedavid.myride.data.local.entity.RideHistory

class RideRepository (
    private val fareCalculator: FareCalculator,
    private val rideHistoryDao: RideHistoryDao
) : IRideRepository {

    override fun estimateFare(
        distanceInKm: Double,
        isPeakHour: Boolean,
        trafficMultiplier: Double
    ): FareEstimate {
        return fareCalculator.calculateFare(distanceInKm, isPeakHour, trafficMultiplier)
    }

    override fun requestRide(): RideConfirmation {
        val drivers = listOf(
            Driver("Bola Uche", "Toyota Prius", "XYZ-1234"),
            Driver("Musa Ibrahim", "Honda Civic", "CODC-5678"),
            Driver("Bruno Alves", "Tesla Model T", "NBFM-6078"),
            Driver("Tiger Woods", "Range Rover Sport", "POFF-5658"),
            Driver("Victor Banjo", "Hyundai 456", "FIF-5678")
        )
        val randomDriver = drivers.random()

        return RideConfirmation(
            status = "confirmed",
            driver = randomDriver,
            estimatedArrival = "5 min"
        )
    }

    override suspend fun saveRideToHistory(
        pickup: LatLng,
        destination: LatLng,
        fare: Double,
        driver: Driver
    ) {
        val ride = RideHistory(
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
        rideHistoryDao.insertRide(ride)
    }
}
