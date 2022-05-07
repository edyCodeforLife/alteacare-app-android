package id.altea.care.view.videocall.chat

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatFragmentProvider {

    @ContributesAndroidInjector
    abstract fun provideChatFragment(): ChatFragment
}
