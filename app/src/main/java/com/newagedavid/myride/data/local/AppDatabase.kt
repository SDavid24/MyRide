package com.newagedavid.myride.data.local

import com.newagedavid.myride.data.local.dao.RideHistoryDao
import com.newagedavid.myride.data.local.entity.RideHistory

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RideHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rideHistoryDao(): RideHistoryDao
}
