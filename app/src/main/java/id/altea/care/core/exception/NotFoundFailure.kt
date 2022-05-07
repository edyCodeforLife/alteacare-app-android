package id.altea.care.core.exception

import id.altea.care.core.exception.Failure.FeatureFailure

class NotFoundFailure {

    class DataNotExist() : FeatureFailure()

    class DataPromotionNotExist() : FeatureFailure()

    class EmptyListLoadMore() : FeatureFailure()
}
