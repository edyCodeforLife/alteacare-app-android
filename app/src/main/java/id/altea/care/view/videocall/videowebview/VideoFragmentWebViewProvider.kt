package id.altea.care.view.videocall.videowebview

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.altea.care.view.videocall.videofragment.VideoFragment

@Module
abstract class VideoFragmentWebViewProvider {

    @ContributesAndroidInjector
    abstract fun provideVideoFragmentWebViewProvider(): VideoFragmentWebView

}