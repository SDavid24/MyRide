package com.newagedavid.myride

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.newagedavid.myride.data.local.AppDatabase
import com.newagedavid.myride.data.local.dao.RideHistoryDao
import com.newagedavid.myride.data.local.entity.RideHistory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class RideHistoryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: RideHistoryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.rideHistoryDao()
    }

    @Test
    fun insertAndRetrieveRide() = runBlocking {
        val ride = RideHistory(
            pickupLat = 1.0,
            pickupLng = 1.0,
            destinationLat = 2.0,
            destinationLng = 2.0,
            fare = 10.0,
            timestamp = System.currentTimeMillis(),
            driverName = "John Doe",
            car = "Toyota Prius",
            plateNumber = "XYZ-1234"
        )

        dao.insertRide(ride)

        val result = dao.getAllRides().first()
        assertEquals(1, result.size)
        assertEquals(ride.driverName, result.first().driverName)
    }
}
