package id.altea.care.view.videocall.videofragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VideoFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideVideoFragment(): VideoFragment

}
