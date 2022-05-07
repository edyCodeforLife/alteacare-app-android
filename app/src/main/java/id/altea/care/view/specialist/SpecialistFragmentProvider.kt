package id.altea.care.view.specialist

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.altea.care.view.home.HomeFragment

@Module
abstract class SpecialistFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideSpecialistFragment(): SpecialistFragment

}
