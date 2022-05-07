package id.altea.care.view.consultation.failure

import id.altea.care.core.exception.Failure

open class PaymentPageFailure : Failure.FeatureFailure() {
    class DetailAppointmentUnload : PaymentPageFailure()
}
