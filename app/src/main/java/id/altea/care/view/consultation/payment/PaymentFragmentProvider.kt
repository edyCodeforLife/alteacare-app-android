package id.altea.care.view.consultation.payment

import dagger.Module

import dagger.android.ContributesAndroidInjector

@Module
abstract class PaymentFragmentProvider {

    @ContributesAndroidInjector
    abstract fun providePaymentFragment(): PaymentFragment
}