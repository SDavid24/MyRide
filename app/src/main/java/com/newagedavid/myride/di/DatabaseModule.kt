package com.newagedavid.myride.di

import androidx.room.Room
import com.newagedavid.myride.data.local.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "ride_history_db"
        ).build()
    }

    single { get<AppDatabase>().rideHistoryDao() }
}
