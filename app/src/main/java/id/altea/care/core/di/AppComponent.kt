package id.altea.care.core.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import id.altea.care.AlteaApps
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class
    ]
)

interface AppComponent : AndroidInjector<AlteaApps> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: AlteaApps): Builder

        fun build(): AppComponent
    }
}
