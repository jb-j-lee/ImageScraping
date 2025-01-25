package com.myjb.dev.di

import com.myjb.dev.model.Repository
import com.myjb.dev.model.RepositoryImp
import com.myjb.dev.model.data.Constants
import com.myjb.dev.model.remote.api.ApiService
import com.myjb.dev.model.remote.datasource.DataSource
import com.myjb.dev.model.remote.converter.JsoupConverter
import com.myjb.dev.model.remote.datasource.RetrofitDataSource
import com.myjb.dev.myapplication.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RetrofitAnnotation

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.HEADERS
                }
            )
        }

        val connectionPool = ConnectionPool(5, 5, TimeUnit.MINUTES)

        val dispatcher = Dispatcher().apply {
            maxRequests = 64
            maxRequestsPerHost = 5
        }

        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectionPool(connectionPool)
            .dispatcher(dispatcher)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(object : Converter.Factory() {
                override fun responseBodyConverter(
                    type: Type,
                    annotations: Array<Annotation>,
                    retrofit: Retrofit,
                ): Converter<ResponseBody, *> {
                    return JsoupConverter(isOnlySection = false)
                }
            })
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @RetrofitAnnotation
    @Provides
    fun provideRetrofitDataSource(apiService: ApiService): DataSource {
        return RetrofitDataSource(apiService)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        @RetrofitAnnotation retrofitDataSource: DataSource,
    ): Repository {
        return RepositoryImp(
            retrofitDataSource = retrofitDataSource
        )
    }
}