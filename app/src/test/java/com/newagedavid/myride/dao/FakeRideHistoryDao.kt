package com.newagedavid.myride.dao

import com.newagedavid.myride.data.local.dao.RideHistoryDao
import com.newagedavid.myride.data.local.entity.RideHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRideHistoryDao : RideHistoryDao {

    val storedRides = mutableListOf<RideHistory>()

    override suspend fun insertRide(ride: RideHistory) {
        storedRides.add(ride)
    }

    override fun getAllRides(): Flow<List<RideHistory>> {
        return flowOf(storedRides)
    }

    override suspend fun clearRideHistory() {
        TODO("Not yet implemented")
    }
}
