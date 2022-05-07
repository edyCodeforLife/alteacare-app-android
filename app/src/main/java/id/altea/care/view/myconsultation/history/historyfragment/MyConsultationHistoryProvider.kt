package id.altea.care.view.myconsultation.history.historyfragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MyConsultationHistoryProvider {

    @ContributesAndroidInjector
    abstract fun provideHistoryConsultationFragment(): MyConsultationHistoryFragment
}
