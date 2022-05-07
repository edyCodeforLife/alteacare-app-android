package id.altea.care.view.consultationdetail.medicaldocument

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MedicalDocumentProvider {

    @ContributesAndroidInjector
    abstract fun provideMedicalDocumentFragment(): MedicalDocumentFragment
}
