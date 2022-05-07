package id.altea.care.view.myconsultation.history.cancelfragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MyConsultationCancelProvider {

    @ContributesAndroidInjector
    abstract fun provideCancelConsultationFragment(): MyConsultationCancelFragment
}
