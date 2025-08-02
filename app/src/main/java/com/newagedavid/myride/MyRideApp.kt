package com.newagedavid.myride

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.newagedavid.myride.di.appModule
import com.newagedavid.myride.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import java.util.Locale


class MyRideApp : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)

            androidContext(this@MyRideApp)
            modules(databaseModule, appModule)
        }
    }
}
