package id.altea.care.core.domain.cache

interface OnboardingCache {
    fun setIsOnBoarding(status : Boolean)
    fun getIsOnBoarding() : Boolean
}