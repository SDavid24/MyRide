package com.newagedavid.myride.data.repository


import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.newagedavid.myride.data.remote.HttpRoutes.GOOGLE_MAPS_URL
import com.newagedavid.myride.data.remote.model.DirectionsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class DirectionsRepository  (
    private val client: HttpClient,
) {

    suspend fun getRoutePolyline(
        origin: LatLng,
        destination: LatLng,
        apiKey: String
    ): List<LatLng> {
        return try {
            val response = getRoute(
                origin = "${origin.latitude},${origin.longitude}",
                destination = "${destination.latitude},${destination.longitude}",
                apiKey = apiKey
            )

            response.routes.firstOrNull()?.overviewPolyline?.points?.let { encodedPolyline ->
                PolyUtil.decode(encodedPolyline)
            } ?: emptyList()

        } catch (e: Exception) {
            Log.e("DirectionsRepo", "Failed to fetch directions", e)
            emptyList()
        }
    }

    //make the api call
    private suspend fun getRoute(origin: String, destination: String, apiKey: String): DirectionsResponse {
        return try {
            client.get(GOOGLE_MAPS_URL) {
                parameter("origin", origin)
                parameter("destination", destination)
                parameter("key", apiKey)
            }.body()

        } catch (e: RedirectResponseException) {
            // 3xx - responses
            throw e
        } catch (e: ClientRequestException) {
            // 4xx - responses
            throw e
        } catch (e: Exception) {
            throw e
        }
    }


}
