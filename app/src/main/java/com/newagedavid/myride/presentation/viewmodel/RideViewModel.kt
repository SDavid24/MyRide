package com.newagedavid.myride.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.newagedavid.myride.data.common.model.Driver
import com.newagedavid.myride.data.common.model.FareEstimate
import com.newagedavid.myride.data.common.model.RideConfirmation
import com.newagedavid.myride.data.common.utils.DistanceUtils
import com.newagedavid.myride.data.local.dao.RideHistoryDao
import com.newagedavid.myride.data.local.entity.RideHistory
import com.newagedavid.myride.data.repository.RideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RideViewModel @Inject constructor(
    private val rideRepository: RideRepository,
    private val rideHistoryDao: RideHistoryDao
) : ViewModel() {

    private val _fareEstimate = MutableStateFlow<FareEstimate?>(null)
    var fareEstimate: StateFlow<FareEstimate?> = _fareEstimate

    fun calculateFareEstimate(
        polyline: List<LatLng>,
        isPeakHour: Boolean,
        trafficMultiplier: Double
    ) {
        val distanceKm = DistanceUtils.calculateDistanceInKm(polyline)
        val estimate = rideRepository.estimateFare(distanceKm, isPeakHour, trafficMultiplier)
        Log.d("RideViewModel", "Fare calculated: $estimate")

        _fareEstimate.value = estimate
    }

    fun requestRide(onComplete: (RideConfirmation) -> Unit) {
        viewModelScope.launch {
            delay(2000) // simulate backend delay
            val response = rideRepository.requestRide()
            onComplete(response)
        }
    }

    fun saveRideToHistory(
        pickup: LatLng,
        destination: LatLng,
        fare: Double,
        driver: Driver
    ) {
        viewModelScope.launch {
            rideRepository.saveRideToHistory(pickup, destination, fare, driver)
        }
    }


    fun confirmRide(
        pickup: LatLng,
        destination: LatLng,
        onComplete: (RideHistory) -> Unit
    ) {
        viewModelScope.launch {
            delay(2000) // simulate progress delay

            val randomDriver = listOf(
                Triple("Bola Uche", "Toyota Prius", "XYZ-1234"),
                Triple("Musa Ibrahim", "Honda Civic", "CODC-5678"),
                Triple("Bruno Alves", "Tesla Model T", "NBFM-6078"),
                Triple("Tiger Woods", "Range Rover Sport", "POFF-5658"),
                Triple("Victor Banjo", "Hyundai 456", "FIF-5678")
            ).random()

            val ride = RideHistory(
                pickupLat = pickup.latitude,
                pickupLng = pickup.longitude,
                destinationLat = destination.latitude,
                destinationLng = destination.longitude,
                fare = _fareEstimate.value?.totalFare ?: 0.0,
                timestamp = System.currentTimeMillis(),
                driverName = randomDriver.first,
                car = randomDriver.second,
                plateNumber = randomDriver.third
            )

            rideHistoryDao.insertRide(ride)
            onComplete(ride)
        }
    }

}
