package com.niceord.agent.networks

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit? {
        if (retrofit == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val original = chain.request()
                    //header
                    val request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        //.header("access-token", Utils.access_token)
                       // .header("client", Utils.client)
                        //.header("expiry", Utils.expiry)
                       // .header("uid", Utils.uid)
                       // .method(original.method(), original.body())
                        .build()
                    return@Interceptor chain.proceed(request)
                })
                .addInterceptor(interceptor)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build()
            retrofit = Retrofit.Builder()
               // .baseUrl(APIService.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}