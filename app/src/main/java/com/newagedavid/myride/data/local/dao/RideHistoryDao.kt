package com.newagedavid.myride.data.local.dao

import androidx.room.*
import com.newagedavid.myride.data.local.entity.RideHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface RideHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: RideHistory)

    @Query("SELECT * FROM ride_history ORDER BY timestamp DESC")
    fun getAllRides(): Flow<List<RideHistory>>

    @Query("DELETE FROM ride_history")
    suspend fun clearRideHistory()
}
