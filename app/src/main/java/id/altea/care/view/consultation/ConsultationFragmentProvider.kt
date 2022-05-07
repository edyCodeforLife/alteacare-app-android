package id.altea.care.view.consultation

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ConsultationFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideConsultationFragment(): ConsultationFragment
}