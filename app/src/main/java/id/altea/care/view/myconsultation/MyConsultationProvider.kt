package id.altea.care.view.myconsultation

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.altea.care.view.myconsultation.history.cancelfragment.MyConsultationCancelProvider
import id.altea.care.view.myconsultation.history.historyfragment.MyConsultationHistoryProvider
import id.altea.care.view.myconsultation.ongoing.OnGoingProvider

@Module
abstract class MyConsultationProvider {

    @ContributesAndroidInjector(
        modules = [
            OnGoingProvider::class,
            MyConsultationHistoryProvider::class,
            MyConsultationCancelProvider::class
        ]
    )
    abstract fun provideMyConsultationFragment(): MyConsultationFragment
}
