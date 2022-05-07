package id.altea.care.view.myconsultation.conversation

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HistoryConversationProvider {

    @ContributesAndroidInjector
    abstract fun provideConversationFragment(): HistoryConversationFragment
}
