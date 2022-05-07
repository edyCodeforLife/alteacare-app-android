package id.altea.care.view.consultationdetail.medicalresume

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MedicalResumeProvider {

    @ContributesAndroidInjector
    abstract fun provideMedicalResumeFragment(): MedicalResumeFragment
}
