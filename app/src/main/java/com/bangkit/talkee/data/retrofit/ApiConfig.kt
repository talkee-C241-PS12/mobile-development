package com.bangkit.talkee.data.retrofit

import android.util.Log
import com.bangkit.talkee.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object{
        fun getApiService(): ApiService{
            return getApiService(ROUTE_BASE_URL)
        }

        fun getApiService(route: String): ApiService {
            val loggingInterceptor = if(BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val request = chain.request()
                    Log.d("LOGGING INTERCEPTOR", request.url.toString())
                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(if(route != ROUTE_ML_MODEL) {
                    BuildConfig.BASE_URL
                } else {
                    BuildConfig.MODEL_URL
                })
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }

        const val ROUTE_BASE_URL = "ROUTE_BASE_URL"
        const val ROUTE_ML_MODEL = "ROUTE_ML_MODEL"
    }
}