package com.newagedavid.myride.data.remote

import com.newagedavid.myride.data.remote.model.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface DirectionsService {
    @GET("directions/json")
    suspend fun getRoute(
        @Query("origin") origin: String,  // e.g., "6.5244,3.3792"
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): DirectionsResponse
}