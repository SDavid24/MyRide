package com.newagedavid.myride.di

import com.newagedavid.myride.data.repository.IRideRepository
import com.newagedavid.myride.data.repository.RideRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindRideRepository(
        rideRepository: RideRepository
    ): IRideRepository
}
