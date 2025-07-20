package com.newagedavid.myride.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.newagedavid.myride.data.repository.DirectionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MapViewModel @Inject constructor(
    private val directionsRepository: DirectionsRepository
) : ViewModel() {

    // Internal mutable state
    private val _pickupLocation = MutableStateFlow<LatLng?>(null)
    private val _destinationLocation = MutableStateFlow<LatLng?>(null)
    private val _routePolyline = MutableStateFlow<List<LatLng>>(emptyList())

    // Exposed immutable state
    val pickupLocation: StateFlow<LatLng?> = _pickupLocation
    val destinationLocation: StateFlow<LatLng?> = _destinationLocation
    val routePolyline: StateFlow<List<LatLng>> = _routePolyline

    // Store API key to avoid repeating it
    private var apiKey: String? = null

    /**
     * Sets the pickup location and attempts to fetch route if destination exists.
     */
    fun setPickupLocation(location: LatLng) {
        _pickupLocation.value = location
        maybeFetchRoute()
    }

    /**
     * Sets the destination location and attempts to fetch route if pickup exists.
     */
    fun setDestinationLocation(location: LatLng?, key: String) {
        _destinationLocation.value = location
        apiKey = key
        maybeFetchRoute()
    }

    /**
     * Clears the current route and destination (used when resetting the state).
     */
    fun clearRoute() {
        _routePolyline.value = emptyList()
        _destinationLocation.value = null
    }

    /**
     * Fetches the route if both pickup and destination are set.
     */
    private fun maybeFetchRoute() {
        val origin = _pickupLocation.value
        val destination = _destinationLocation.value
        val key = apiKey

        if (origin != null && destination != null && !key.isNullOrBlank()) {
            viewModelScope.launch {
                val polyline = directionsRepository.getRoutePolyline(origin, destination, key)
                Log.d("MapViewModel", "Fetched ${polyline.size} polyline points")

                _routePolyline.value = polyline
            }
        }
    }
}
