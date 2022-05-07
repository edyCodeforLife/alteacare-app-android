package id.altea.care.core.di

import dagger.Module
import dagger.Provides
import id.altea.care.core.data.network.api.*
import id.altea.care.core.data.repositoryimpl.*
import id.altea.care.core.domain.repository.*

@Module
class RepositoryModule {

    @Provides
    fun provideUserRepository(userApi: UserApi): UserRepository {
        return UserRepositoryImpl(userApi)
    }

    @Provides
    fun provideDataRepository(dataApi: DataApi): DataRepository {
        return DataRepositoryImpl(dataApi)
    }

    @Provides
    fun providePaymentRepository(paymentApi: PaymentApi) : PaymentRepository{
        return PaymentRespositoryImpl(paymentApi)
    }

    @Provides
    fun provideAppoimentRepository(appointmentApi: AppointmentApi): AppointmentRepository {
        return AppointmentRepositoryImpl(appointmentApi)
    }

    @Provides
    fun provideMarketingRepository(marketingApi: MarketingApi) : MarketingRepository {
        return MarketingRespositoryImpl(marketingApi)
    }
}
