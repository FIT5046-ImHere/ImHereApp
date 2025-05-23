package com.example.imhere.model.service.module

import android.content.Context
import android.util.Log
import com.example.imhere.model.service.CalendarApi
import com.example.imhere.model.service.CalendarService
import com.example.imhere.model.service.impl.CalendarServiceImpl
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking

@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {

    @Provides @Singleton
    fun provideAuthInterceptor(
        @ApplicationContext ctx: Context
    ): Interceptor = Interceptor { chain ->

        val acct = GoogleSignIn.getLastSignedInAccount(ctx)
            ?: throw IllegalStateException("Not signed in")

        val scope = "oauth2:https://www.googleapis.com/auth/calendar"
        val accessToken = GoogleAuthUtil.getToken(ctx, acct.account!!, scope)
        Log.d("CalendarAccessToken", accessToken)
        // 2. Extract the ID token (make sure you called requestIdToken(...) in your GoogleSignInOptions)
        val idToken = acct.idToken
            ?: throw IllegalStateException("No ID token available")

        // 3. Add it as a Bearer header
        val authed = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        // 1. Grab the signed-in account
//        val accessToken = runBlocking {
//            FirebaseAuth.getInstance().currentUser
//                ?.getIdToken(/* forceRefresh = */ true)
//                ?.await()
//                ?.token
//        } ?: throw IllegalStateException("No access token available")
//
//        Log.d("Interceptor AcesssTok", accessToken)
//
//        val authed = chain.request()
//            .newBuilder()
//            .addHeader("Authorization", "Bearer $accessToken")
//            .build()

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
