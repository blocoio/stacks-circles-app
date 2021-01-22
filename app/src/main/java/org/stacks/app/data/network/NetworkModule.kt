package org.stacks.app.data.network

import android.app.Application
import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.sianaki.flowretrofitadapter.FlowCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.stacks.app.BuildConfig
import org.stacks.app.R
import org.stacks.app.data.network.models.RegistrarName
import org.stacks.app.data.network.models.RegistrarNameStatusAdapter
import org.stacks.app.data.network.services.GaiaService
import org.stacks.app.data.network.services.HubService
import org.stacks.app.data.network.services.RegistrarService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {

    @Provides
    fun resources(application: Application) = application.resources

    @Provides
    fun gson() =
        GsonBuilder()
            .registerTypeAdapter(
                RegistrarName.RegistrarNameStatus::class.java,
                RegistrarNameStatusAdapter()
            )
            .create()

    @Singleton
    @Provides
    @GaiaBaseUrl
    fun gaiaBaseUrl(resources: Resources) = resources.getString(R.string.gaia_endpoint)

    @Singleton
    @Provides
    @HubBaseUrl
    fun hubBaseUrl(resources: Resources) = resources.getString(R.string.hub_endpoint)

    @Singleton
    @Provides
    @RegistrarBaseUrl
    fun registrarBaseUrl(resources: Resources) = resources.getString(R.string.registrar_endpoint)

    @Provides
    fun provideConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    fun loggingInterceptor() =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    fun socketFactory(): DelegatingSocketFactory =
        DelegatingSocketFactory(javax.net.SocketFactory.getDefault())


    @Provides
    @Singleton
    fun httpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        socketFactory: DelegatingSocketFactory,
    ) =
        OkHttpClient.Builder()
            .socketFactory(socketFactory)
            .apply { if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor) }
            .build()

    @Provides
    @Singleton
    @GaiaRetrofit
    fun gaiaRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory,
        @GaiaBaseUrl baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addCallAdapterFactory(FlowCallAdapterFactory.create())
        .addConverterFactory(converterFactory)
        .baseUrl(baseUrl)
        .build()

    @Provides
    fun provideGaiaService(@GaiaRetrofit retrofit: Retrofit): GaiaService =
        retrofit.create(GaiaService::class.java)

    @Provides
    @Singleton
    @HubRetrofit
    fun hubRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory,
        @HubBaseUrl baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addCallAdapterFactory(FlowCallAdapterFactory.create())
        .addConverterFactory(converterFactory)
        .baseUrl(baseUrl)
        .build()

    @Provides
    fun provideHubService(@HubRetrofit retrofit: Retrofit): HubService =
        retrofit.create(HubService::class.java)

    @Provides
    @Singleton
    @RegistrarRetrofit
    fun registrarRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory,
        @RegistrarBaseUrl baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addCallAdapterFactory(FlowCallAdapterFactory.create())
        .addConverterFactory(converterFactory)
        .baseUrl(baseUrl)
        .build()

    @Provides
    fun provideRegistrarService(@RegistrarRetrofit retrofit: Retrofit): RegistrarService =
        retrofit.create(RegistrarService::class.java)

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class GaiaBaseUrl

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class GaiaRetrofit

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class HubBaseUrl

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class HubRetrofit

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RegistrarBaseUrl

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RegistrarRetrofit

}