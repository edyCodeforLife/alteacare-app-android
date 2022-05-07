package id.altea.care.view.specialistsearch.bottomsheet.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.altea.care.view.specialistsearch.bottomsheet.FilterAllHospitalBottomSheet
import id.altea.care.view.specialistsearch.bottomsheet.FilterAllSpecialistBottomSheet
import id.altea.care.view.specialistsearch.bottomsheet.SpecialistFilterBottomSheet

@Module
abstract class SpecialistFilterBottomSheetModule {

    @ContributesAndroidInjector
    abstract fun provideFilterBottomSheet(): SpecialistFilterBottomSheet

    @ContributesAndroidInjector
    abstract fun provideFilterAllSpecialist(): FilterAllSpecialistBottomSheet

    @ContributesAndroidInjector
    abstract fun provideFilterAllHospital(): FilterAllHospitalBottomSheet
}
