package id.altea.care.core.di

import dagger.Module
import dagger.Provides
import id.altea.care.core.data.network.api.*
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDataApi(@Named("public-api") retrofit: Retrofit): DataApi {
        return retrofit.create(DataApi::class.java)
    }


    @Provides
    @Singleton
    fun provideMarketingApi(@Named("public-api") retrofit: Retrofit) : MarketingApi{
        return retrofit.create(MarketingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppoimentApi(retrofit: Retrofit): AppointmentApi {
        return retrofit.create(AppointmentApi::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentApi(retrofit: Retrofit) : PaymentApi {
        return retrofit.create(PaymentApi::class.java)
    }
}

