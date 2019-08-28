package dev.sunnat629.barcodescan.di.components

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dev.sunnat629.barcodescan.di.modules.AppModule
import dev.sunnat629.barcodescan.di.modules.RetrofitFactoryModule
import dev.sunnat629.barcodescan.di.scope.ApplicationScope
import javax.inject.Singleton

@ApplicationScope
@Singleton
@Component(
    modules = [
        RetrofitFactoryModule::class,
        AppModule::class
    ]
)
interface AppComponent: AppComponentDefault {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AppComponent
    }
}