package id.altea.care.view.myconsultation.ongoing

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OnGoingProvider {

    @ContributesAndroidInjector
    abstract fun provideOnGoingFragment(): OnGoingFragment
}
