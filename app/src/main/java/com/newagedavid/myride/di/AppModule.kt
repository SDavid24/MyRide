package com.newagedavid.myride.di

import com.newagedavid.myride.data.common.utils.FareCalculator
import com.newagedavid.myride.data.remote.KtorClientProvider
import com.newagedavid.myride.data.repository.DirectionsRepository
import com.newagedavid.myride.data.repository.IRideRepository
import com.newagedavid.myride.data.repository.PlacesRepository
import com.newagedavid.myride.data.repository.RideRepository
import com.newagedavid.myride.presentation.viewmodel.MapViewModel
import com.newagedavid.myride.presentation.viewmodel.RideHistoryViewModel
import com.newagedavid.myride.presentation.viewmodel.RideInputViewModel
import com.newagedavid.myride.presentation.viewmodel.RideViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Ktor Client
    single { KtorClientProvider.client }

    // Fare calculator
    single { FareCalculator() }

    // Repositories
    single<IRideRepository> { RideRepository(get(), get()) }

    single { DirectionsRepository(get()) }
    single { PlacesRepository(get()) }


    // ViewModels
    viewModel { MapViewModel(get()) }
    viewModel { RideViewModel(get(), get()) }
    viewModel { RideHistoryViewModel(get()) }
    viewModel { RideInputViewModel(get()) }

}
