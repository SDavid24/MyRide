package com.newagedavid.myride.data.common.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

object DistanceUtils {

    fun calculateDistanceInKm(polyline: List<LatLng>): Double {
        var totalMeters = 0.0
        for (i in 0 until polyline.size - 1) {
            totalMeters += SphericalUtil.computeDistanceBetween(polyline[i], polyline[i + 1])
        }
        return totalMeters / 1000.0 // convert to kilometers
    }
}