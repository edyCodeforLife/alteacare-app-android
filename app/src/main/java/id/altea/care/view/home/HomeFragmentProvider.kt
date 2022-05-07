package id.altea.care.view.home

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.altea.care.view.home.bottomsheet.LiveChatBottomSheet

@Module
abstract class HomeFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideHomeFragment(): HomeFragment
}
