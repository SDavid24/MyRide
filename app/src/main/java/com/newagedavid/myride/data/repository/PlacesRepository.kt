package com.newagedavid.myride.data.repository

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Repository that handles fetching place suggestions from Google Places API.
 * This belongs to the data layer because it deals with external services.
 */
class PlacesRepository(context: Context) {

    private val placesClient: PlacesClient = Places.createClient(context)

    // Token to group autocomplete queries in a session (improves relevance and billing)
    private val sessionToken = AutocompleteSessionToken.newInstance()

    /**
     * Fetches autocomplete predictions for a given query.
     *
     * @param query The string input from the user
     * @return List of place predictions (could be empty)
     */
    suspend fun getAutocompleteSuggestions(query: String): List<AutocompletePrediction> {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        val response = placesClient.findAutocompletePredictions(request).await()

        return response.autocompletePredictions
    }

    suspend fun getLatLngFromPlaceId(placeId: String, context: Context): LatLng? {
        return suspendCancellableCoroutine { continuation ->
            val placesClient = Places.createClient(context)

            val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG)).build()
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    val place = response.place
                    continuation.resume(place.latLng) {}
                }
                .addOnFailureListener { exception ->
                    Log.e("PlacesRepository", "Place not found: ${exception.message}")
                    continuation.resume(null) {}
                }
        }
    }


    suspend fun getLatLngFromAddress(address: String, context: Context, apiKey: String): LatLng? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)
                val results = geocoder.getFromLocationName(address, 1)
                if (!results.isNullOrEmpty()) {
                    val location = results[0]
                    LatLng(location.latitude, location.longitude)
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}

