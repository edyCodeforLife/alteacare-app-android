package id.altea.care.core.di

import android.util.Log
//import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import id.altea.care.AlteaApps
import id.altea.care.BuildConfig
import id.altea.care.core.data.network.api.AlteaCareInterceptor
import id.altea.care.core.domain.cache.AccountCache
import id.altea.care.core.domain.cache.AuthCache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    @Named("public-api")
    fun provideRetrofitPublic(@Named("public") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("public")
    fun providesOkhttpClient3(alteaCareInterceptor: AlteaCareInterceptor,application : AlteaApps): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(alteaCareInterceptor)
            //.addInterceptor(ChuckerInterceptor(application))
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(httpLoggingInterceptor())
                    addNetworkInterceptor(StethoInterceptor())
                }
            }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofitAltea(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesOkhttpClient(alteaCareInterceptor: AlteaCareInterceptor,application : AlteaApps): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(alteaCareInterceptor)
            //.addInterceptor(ChuckerInterceptor(application))
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(httpLoggingInterceptor())
                    addNetworkInterceptor(StethoInterceptor())
                }
            }.build()
    }

    @Provides
    @Singleton
    fun provideDefaultInterceptor(authCache: AuthCache, accountCache: AccountCache): Interceptor {
        return AlteaCareInterceptor(authCache, accountCache)
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Log.d("Retrofit Profiler", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY

        }
    }
}
