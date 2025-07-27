package com.newagedavid.myride.presentation.ui.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Gets the user's last known location using Google's FusedLocationProviderClient.
 * This requires location permissions to already be granted.
 */
@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    return suspendCancellableCoroutine { cont ->
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 1000
            numUpdates = 1
        }

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                cont.resume(result.lastLocation)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
    }
}


/**
 * Converts a Location object (latitude, longitude) into a human-readable address string
 * using Android's Geocoder.
 */
fun getAddressFromLocation2(context: Context, location: Location): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    return addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
}

suspend fun getAddressFromLocation(context: Context, location: Location): String {
    return withContext(Dispatchers.IO) {
        try {
            val addresses = Geocoder(context, Locale.getDefault())
                .getFromLocation(location.latitude, location.longitude, 1)

            if (addresses?.isNotEmpty() == true) {
                addresses[0]?.getAddressLine(0)
            } else {
                "Unknown location"
            }
        } catch (e: IOException) {
            Log.e("Geocoder", "Error: ${e.localizedMessage}")
            "Address unavailable"
        }.toString()
    }
}


// Location permission handling
fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}


suspend fun zoomToFitRoute(
    pickup: LatLng,
    destination: LatLng,
    cameraPositionState: CameraPositionState
) {
    val boundsBuilder = LatLngBounds.builder()
    boundsBuilder.include(pickup)
    boundsBuilder.include(destination)

    val bounds = boundsBuilder.build()
    val padding = 150 // pixels

    cameraPositionState.animate(
        update = CameraUpdateFactory.newLatLngBounds(bounds, padding),
        durationMs = 1000
    )
}