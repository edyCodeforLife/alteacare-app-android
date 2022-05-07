package id.altea.care.view.home.failure

import id.altea.care.core.exception.Failure

class HomeFailure {

    class HomeSpecializationFailure: Failure.FeatureFailure()

    class HomeBannerClickNeedLogin : Failure.FeatureFailure()
}
