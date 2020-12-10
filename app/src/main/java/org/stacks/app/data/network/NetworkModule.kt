package org.stacks.app.data.network

import android.app.Application
import android.content.res.Resources
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.stacks.app.BuildConfig
import org.stacks.app.R
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {

    @Provides
    fun resources(application: Application) = application.resources

    @Provides
    fun gson() = Gson()

    @Provides
    @Singleton
    @BaseUrl
    fun baseUrl(resources: Resources) = resources.getString(R.string.api_url)

    @Provides
    @Singleton
    fun provideCallAdapterFactory(): RxJava2CallAdapterFactory =
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

    @Provides
    fun provideConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    fun loggingInterceptor() =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun httpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @BaseUrl baseUrl: String
    ) =
        OkHttpClient.Builder()
            .apply { if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor) }
            .build()

    @Provides
    @Singleton
    @GaiaRetrofit
    fun gaiaRetrofit(
        okHttpClient: OkHttpClient,
        callAdapterFactory: RxJava2CallAdapterFactory,
        converterFactory: GsonConverterFactory,
        @BaseUrl baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addCallAdapterFactory(callAdapterFactory)
        .addConverterFactory(converterFactory)
        .baseUrl(baseUrl)
        .build()

    @Provides
    fun provideGaiaHubService(@GaiaRetrofit retrofit: Retrofit): GaiaHubService =
        retrofit.create(GaiaHubService::class.java)

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class BaseUrl

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class GaiaRetrofit
}