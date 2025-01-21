package com.myjb.dev.di

import com.myjb.dev.model.Repository
import com.myjb.dev.model.RepositoryImp
import com.myjb.dev.model.remote.datasource.DataSource
import com.myjb.dev.model.remote.datasource.JsoupDataSource
import com.myjb.dev.model.remote.datasource.RetrofitDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class JsoupAnnotation

@Module
@InstallIn(SingletonComponent::class)
object JsoupModule {

    @Singleton
    @JsoupAnnotation
    @Provides
    fun provideJsoupDataSource(): DataSource {
        return JsoupDataSource
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RetrofitAnnotation

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @RetrofitAnnotation
    @Provides
    fun provideRetrofitDataSource(): DataSource {
        return RetrofitDataSource
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        @JsoupAnnotation jsoupDataSource: DataSource,
        @RetrofitAnnotation retrofitDataSource: DataSource,
    ): Repository {
        return RepositoryImp(
            jsoupDataSource = jsoupDataSource,
            retrofitDataSource = retrofitDataSource
        )
    }
}