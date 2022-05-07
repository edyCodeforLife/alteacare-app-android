package id.altea.care.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import id.altea.care.core.data.local.cachesource.*
import id.altea.care.core.data.local.room.DbConfig
import id.altea.care.core.data.local.room.dao.AccountDao
import id.altea.care.core.domain.cache.*
import javax.inject.Singleton

@Module
class CacheModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideRoom(context: Context): DbConfig = Room
        .databaseBuilder(context, DbConfig::class.java, "db_alteacare")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideAuthCache(sharedPreferences: SharedPreferences): AuthCache {
        return AuthCacheSource(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideOnboardingCache(sharedPreferences: SharedPreferences) : OnboardingCache{
        return OboardingCacheSource(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideUserProfileCache(sharedPreferences: SharedPreferences): UserCache {
        return UserCacheSource(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideShareScreenCache(sharedPreferences: SharedPreferences): ShareScreenCache {
        return ShareScreenSource(sharedPreferences)
    }

    @Provides
    fun provideAccountCache(accountDao: AccountDao): AccountCache {
        return AccountCacheSource(accountDao)
    }

    @Provides
    fun provideAccountDao(dbConfig: DbConfig): AccountDao = dbConfig.accountDao()

    @Singleton
    @Provides
    fun provideAnalyticsCache(sharedPreferences: SharedPreferences): AnalyticsCache {
        return AnalyticsCacheSource(sharedPreferences)
    }

}
