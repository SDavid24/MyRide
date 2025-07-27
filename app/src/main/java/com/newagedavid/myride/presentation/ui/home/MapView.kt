package com.newagedavid.myride.presentation.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.newagedavid.myride.presentation.ui.utils.hasLocationPermission

@SuppressLint("MissingPermission")
@Composable
fun GoogleMapView(
    currentLocation: LatLng?,
    cameraPositionState: CameraPositionState, ///positions the screen to the user's current location
    routePolyline: List<LatLng> // new parameter
) {

    GoogleMap(
        modifier = Modifier.fillMaxSize()
            .padding(WindowInsets.navigationBars.asPaddingValues()),  // prevents overlap with nav bar
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = hasLocationPermission(LocalContext.current)
        )
    ) {
        currentLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Pickup"
            )
        }


        if (routePolyline.isNotEmpty()) {
            Polyline(
                points = routePolyline,
                color = Color.Blue,
                width = 8f
            )

            Marker(        // Show a marker only if we have a location
                state = MarkerState(position = routePolyline.last()),
                title = "Destination"
            )
        }
    }

}

