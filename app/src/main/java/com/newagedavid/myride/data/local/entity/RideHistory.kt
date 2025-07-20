package com.newagedavid.myride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ride_history")
data class RideHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pickupLat: Double,
    val pickupLng: Double,
    val destinationLat: Double,
    val destinationLng: Double,
    val fare: Double,
    val timestamp: Long,
    val driverName: String,
    val car: String,
    val plateNumber: String
)
