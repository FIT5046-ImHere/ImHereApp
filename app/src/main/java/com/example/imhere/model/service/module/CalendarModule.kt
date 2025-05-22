package com.example.imhere.model.service.module

import android.content.Context
import com.example.imhere.model.service.CalendarApi
import com.example.imhere.model.service.CalendarService
import com.example.imhere.model.service.impl.CalendarServiceImpl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {

    @Provides @Singleton
    fun provideAuthInterceptor(
        @ApplicationContext ctx: Context
    ): Interceptor = Interceptor { chain ->
        // 1. Grab the signed-in account
        val acct = GoogleSignIn.getLastSignedInAccount(ctx)
            ?: throw IllegalStateException("Not signed in")

        // 2. Extract the ID token (make sure you called requestIdToken(...) in your GoogleSignInOptions)
        val idToken = acct.idToken
            ?: throw IllegalStateException("No ID token available")

        // 3. Add it as a Bearer header
        val authed = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $idToken")
            .build()

        chain.proceed(authed)
    }

    @Provides @Singleton
    fun provideOkHttpClient(auth: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(auth)
            .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/calendar/v3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideCalendarApi(retrofit: Retrofit): CalendarApi =
        retrofit.create(CalendarApi::class.java)

    @Provides @Singleton
    fun provideCalendarService(api: CalendarApi): CalendarService =
        CalendarServiceImpl(api)
}
