package dev.sunnat629.barcodescan.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import dev.sunnat629.barcodescan.Toaster
import dev.sunnat629.barcodescan.utils.AppSharedPreferences
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideToaster(context: Application): Toaster {
        return Toaster(context.applicationContext)
    }

    @Singleton
    @Provides
    internal fun provideSharedPreferences(context: Application): AppSharedPreferences {
        return AppSharedPreferences(context.applicationContext)
    }
}