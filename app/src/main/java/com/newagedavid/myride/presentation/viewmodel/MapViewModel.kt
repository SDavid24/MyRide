package com.newagedavid.myride.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.newagedavid.myride.data.repository.DirectionsRepository
import com.newagedavid.myride.data.repository.PlacesRepository
import com.newagedavid.myride.presentation.ui.utils.getAddressFromLocation
import com.newagedavid.myride.presentation.ui.utils.getCurrentLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel (
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

    private val _suggestions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val suggestions: StateFlow<List<AutocompletePrediction>> = _suggestions

    private val _pickupAddressText = MutableStateFlow("")
    val pickupAddressText: StateFlow<String> = _pickupAddressText

    private val _destinationAddressText = MutableStateFlow("")
    val destinationAddressText: StateFlow<String> = _destinationAddressText

    private val _focusedField = MutableStateFlow<String?>(null)
    val focusedField: StateFlow<String?> = _focusedField


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

    fun loadUserLocation(context: Context, onAddressFetched: (String) -> Unit) {
        viewModelScope.launch {
            val location = getCurrentLocation(context)
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                _pickupLocation.value = latLng

                //val address = getAddressFromLocation(context, it)

                viewModelScope.launch {
                    val address = getAddressFromLocation(context, location)
                    onAddressFetched(address)
                }

            }
        }
    }

    fun fetchSuggestions(query: String, context: Context) {
        viewModelScope.launch {
            val results = PlacesRepository(context).getAutocompleteSuggestions(query)
            _suggestions.value = results
        }
    }


    fun selectPlaceFromSuggestion(
        placeId: String,
        context: Context,
        isPickup: Boolean,
        apiKey: String
    ) {
        viewModelScope.launch {
            val latLng = PlacesRepository(context).getLatLngFromPlaceId(placeId, context)
            val address = PlacesRepository(context).getAddressFromPlaceId(placeId, context)

            latLng?.let {
                if (isPickup) {
                    setPickupLocation(it)
                    _pickupAddressText.value = address
                } else {
                    setDestinationLocation(it, apiKey)
                    _destinationAddressText.value = address
                }
            }
        }
    }

    fun updatePickupAddressText(text: String) {
        _pickupAddressText.value = text
    }

    fun onPickupInputChanged(query: String, context: Context) {
        _pickupAddressText.value = query
        viewModelScope.launch {
            fetchSuggestions(query, context)
        }
    }

    fun onFieldFocusChanged(field: String?) {
        _focusedField.value = field
        if (field == null) clearSuggestions()
    }


    fun updateDestinationAddressText(text: String) {
        _destinationAddressText.value = text
    }

    fun onDestinationInputChanged(query: String, context: Context) {
        _destinationAddressText.value = query
        viewModelScope.launch {
            fetchSuggestions(query, context)
        }
    }

    fun updateSuggestions(newSuggestions: List<AutocompletePrediction>) {
        _suggestions.value = newSuggestions
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }

}
