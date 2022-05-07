package id.altea.care.view.consultationdetail.patientdata

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.altea.care.view.common.bottomsheet.CountDownSpecialistBottomSheet

@Module
abstract class PatientDataProvider {

    @ContributesAndroidInjector
    abstract fun providePatientFragment(): PatientDataFragment

    @ContributesAndroidInjector
    abstract fun provideCountDownSpecialistBottomSheet() : CountDownSpecialistBottomSheet
}
