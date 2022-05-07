package id.altea.care.view.doctordetail.bottomsheet

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.altea.care.view.doctordetail.bottomsheet.FamilyMemberListBottomSheet

@Module
abstract class FamilyMemberListBottomSheetModule {

    @ContributesAndroidInjector
    abstract fun bindFamilyMemberListBottomSheet(): FamilyMemberListBottomSheet

}