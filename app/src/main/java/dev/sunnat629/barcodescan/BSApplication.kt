package dev.sunnat629.barcodescan

import android.app.Application
import android.content.Context
import dev.sunnat629.barcodescan.di.components.AppComponent
import dev.sunnat629.barcodescan.di.components.DaggerAppComponent
import timber.log.Timber

class BSApplication : Application(){

    private lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().application(this).build()
        initiateTimber()
    }

    /**
     * This function is to install the instance of the Timber
     * */
    private fun initiateTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object{
        fun appComponent(context: Context): AppComponent {
            return (context.applicationContext as BSApplication).component
        }
    }
}