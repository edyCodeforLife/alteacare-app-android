package id.altea.care.view.address.addresslist.bottomsheet

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AddressListBottomSheetModule {
    @ContributesAndroidInjector
    abstract fun provideAddressBottomSheet(): AddressListOptionBottomSheet
}
