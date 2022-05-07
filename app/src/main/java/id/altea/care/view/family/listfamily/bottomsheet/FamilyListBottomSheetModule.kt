package id.altea.care.view.family.listfamily.bottomsheet

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FamilyListBottomSheetModule {
    @ContributesAndroidInjector
    abstract fun provideFamilyBottomSheet(): FamilyListOptionBottomSheet
}