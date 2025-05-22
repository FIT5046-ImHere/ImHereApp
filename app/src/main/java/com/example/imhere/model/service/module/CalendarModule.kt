package com.example.imhere.model.service.module

import com.example.imhere.model.service.CalendarApi
import com.example.imhere.model.service.CalendarService
import com.example.imhere.model.service.impl.CalendarServiceImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {

    /** Intercepts every request to add the OAuth2 Bearer token header */
    @Provides
    @Singleton
    fun provideOkHttpClient(@Named("accessToken") token: String): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(req)
            }
            .build()

    /** Gson instance for (de)serialization */
    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().create()

    /** Retrofit configured for Google Calendar v3 */
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/calendar/v3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    /** Our Retrofit interface for Calendar endpoints */
    @Provides
    @Singleton
    fun provideCalendarApi(retrofit: Retrofit): CalendarApi =
        retrofit.create(CalendarApi::class.java)

    /** Expose our CalendarService implementation */
    @Provides
    @Singleton
    fun provideCalendarService(api: CalendarApi): CalendarService =
        CalendarServiceImpl(api)
}
