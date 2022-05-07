package id.altea.care.view.consultation.confirm

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ConfirmationFragmentFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideConfirmationFragment(): ConfirmationFragment
}