package id.altea.care.view.account

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AccountFragmentProvider {
    @ContributesAndroidInjector
    abstract fun provideAccountFragment(): AccountFragment
}