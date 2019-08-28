package dev.sunnat629.barcodescan.di.modules

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dev.sunnat629.barcodescan.BuildConfig
import dev.sunnat629.barcodescan.api.BSApiService
import dev.sunnat629.barcodescan.api.DeviceRepository
import dev.sunnat629.barcodescan.utils.AppConstants.SERVER_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RetrofitFactoryModule {

    @Singleton
    @Provides
    fun provideDeviceRepository(okHttpClient: OkHttpClient): DeviceRepository {
        return DeviceRepository(provideRetrofitService(okHttpClient))
    }


    @Singleton
    @Provides
    fun provideRetrofitService(okHttpClient: OkHttpClient): BSApiService {
        return provideMakeRetrofitService(okHttpClient)
            .create(BSApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMakeRetrofitService(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideMakeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            client.addInterceptor(httpLoggingInterceptor)
        }
        return client.build()
    }

    @Singleton
    @Provides
    fun makeLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

}