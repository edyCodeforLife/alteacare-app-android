package id.altea.care.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import id.altea.care.AlteaApps
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.AnalyticManagerImpl
import id.altea.care.core.ext.TagInjection
import id.altea.care.view.deeplink.DeepLinkManager
import id.altea.care.view.deeplink.DeepLinkManagerImpl
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        ViewModelModule::class,
        ActivityBuilderModule::class,
        NetworkModule::class,
        ApiModule::class,
        CacheModule::class,
        RepositoryModule::class
    ]
)
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: AlteaApps): Context {
        return application
    }

    @Named(TagInjection.IO_Scheduler)
    @Singleton
    @Provides
    fun provideIoScheduler(): Scheduler = Schedulers.io()

    @Named(TagInjection.UI_Scheduler)
    @Provides
    @Singleton
    fun provideUiScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @Singleton
    fun provideDeepLinkManager(application: AlteaApps): DeepLinkManager =
        DeepLinkManagerImpl(application)

    @Provides
    @Singleton
    fun provideAnalyticManager(application: AlteaApps): AnalyticManager {
        return AnalyticManagerImpl(application)
    }
}
