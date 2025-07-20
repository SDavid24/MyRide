package com.newagedavid.myride.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.newagedavid.myride.data.local.AppDatabase
import com.newagedavid.myride.data.repository.PlacesRepository
import com.newagedavid.myride.data.local.dao.RideHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "my_ride_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideRideHistoryDao(db: AppDatabase): RideHistoryDao = db.rideHistoryDao()

    @Provides
    @Singleton
    fun providePlacesRepository(@ApplicationContext context: Context): PlacesRepository {
        return PlacesRepository(context)
    }
}
