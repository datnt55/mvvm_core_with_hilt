package com.datnt.core.di

import android.content.Context
import com.datnt.core.di.qualifier.BaseUrl
import com.datnt.core.model.local.preference.PreferenceHelper
import com.datnt.core.utils.Constants
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
   @Provides
    fun provideClient(@ApplicationContext context: Context, preferenceHelper: PreferenceHelper, chuckInterceptor: ChuckInterceptor) : OkHttpClient{
        val builder = OkHttpClient.Builder()
                .readTimeout(Constants.TIME_OUT.toLong(), TimeUnit.SECONDS)
                .connectTimeout(Constants.TIME_OUT.toLong(), TimeUnit.SECONDS)
                .callTimeout(Constants.TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(Constants.TIME_OUT.toLong(), TimeUnit.SECONDS)
                .addInterceptor(chuckInterceptor)
                .addInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC).setLevel(
                                HttpLoggingInterceptor.Level.BODY
                        ).setLevel(HttpLoggingInterceptor.Level.HEADERS)
                )
                .addInterceptor { chain: Interceptor.Chain ->
                    chain.proceed(chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer ${preferenceHelper.token}")
                            .build()
                    )
                }
        return builder.build()
    }

    @Provides
    internal fun provideChuckInterceptor(@ApplicationContext context: Context): ChuckInterceptor = ChuckInterceptor(context)

    @Provides
    fun provideRetrofit(@BaseUrl baseUrl : String, @ApplicationContext context: Context, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
    }
}