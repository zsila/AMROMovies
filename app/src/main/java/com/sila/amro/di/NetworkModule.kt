package com.sila.amro.di

import com.sila.amro.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.TMDB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()



    @Provides
    @Singleton
    fun provideAuthQueryInterceptor(): Interceptor = Interceptor { chain ->
        val key = BuildConfig.TMDB_API_KEY
        if (key.isBlank()) {
            throw IllegalStateException("TMDB_API_KEY is not configured. Add TMDB_API_KEY to local.properties.")
        }

        val original = chain.request()
        val originalUrl = original.url

        val newUrl: HttpUrl = originalUrl.newBuilder()
            .addQueryParameter("api_key", key)
            .addQueryParameter("language", "en-US")
            .build()

        chain.proceed(original.newBuilder().url(newUrl).build())
    }

    @Provides
    @Singleton
    fun provideOkHttp(authQueryInterceptor: Interceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BASIC)
        }
        return OkHttpClient.Builder()
            .addInterceptor(authQueryInterceptor)
            .addInterceptor(logging)
            .build()
    }

}