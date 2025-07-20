package com.newagedavid.myride.data.repository


import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.newagedavid.myride.data.remote.DirectionsService
import javax.inject.Inject

class DirectionsRepository @Inject constructor(
    private val directionsService: DirectionsService
) {

    suspend fun getRoutePolyline(
        origin: LatLng,
        destination: LatLng,
        apiKey: String
    ): List<LatLng> {
        return try {
            val response = directionsService.getRoute(
                origin = "${origin.latitude},${origin.longitude}",
                destination = "${destination.latitude},${destination.longitude}",
                apiKey = apiKey
            )

            if (response.routes.isNotEmpty()) {
                val encodedPolyline = response.routes[0].overviewPolyline.points

                PolyUtil.decode(encodedPolyline) // Decode the encoded polyline string to a list of LatLng
            } else {
                emptyList()
            }

        } catch (e: Exception) {
            Log.e("DirectionsRepo", "Failed to fetch directions", e)
            emptyList()
        }
    }
}
