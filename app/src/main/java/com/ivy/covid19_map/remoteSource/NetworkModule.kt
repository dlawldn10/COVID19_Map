package com.ivy.covid19_map.remoteSource

import android.app.Application
import com.ivy.covid19_map.repository.NetworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/* hilt를 이용하여 네트워크 관련 객체 의존성 주입 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.odcloud.kr/api/15077586/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesService(retrofit: Retrofit): RequestInterface {
        return retrofit.create(RequestInterface::class.java)
    }

    @Provides
    @Singleton
    fun providesNetworkRepository(server: RequestInterface, application: Application): NetworkRepository {
        return NetworkRepository(server, application)
    }
}