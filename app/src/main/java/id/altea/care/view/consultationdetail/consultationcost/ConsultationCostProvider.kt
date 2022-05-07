package id.altea.care.view.consultationdetail.consultationcost

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ConsultationCostProvider {

    @ContributesAndroidInjector
    abstract fun provideConsultationCostFragment(): ConsultationCostFragment
}
