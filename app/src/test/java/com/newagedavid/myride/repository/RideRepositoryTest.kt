package com.newagedavid.myride.repository

import com.google.android.gms.maps.model.LatLng
import com.newagedavid.myride.data.common.model.Driver
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RideRepositoryTest {

    private lateinit var fakeRepo: FakeRideRepository

    @Before
    fun setup() {
        fakeRepo = FakeRideRepository()
    }

    @Test
    fun `requestRide returns driver`() {
        val ride = fakeRepo.requestRide()
        assertEquals("confirmed", ride.status)
    }

    @Test
    fun `saveRideToHistory stores ride`(): Unit = runTest {
        val pickup = LatLng(1.0, 1.0)
        val dest = LatLng(2.0, 2.0)
        val driver = Driver("John", "Car", "XYZ")

        fakeRepo.saveRideToHistory(pickup, dest, 10.0, driver)


        assertEquals(1, fakeRepo.savedRides.size)
    }
}
