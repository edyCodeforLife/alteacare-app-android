package id.altea.care.view.doctordetail.failure

import id.altea.care.core.exception.Failure

sealed class DoctorDetailFailure : Failure.FeatureFailure() {
    object ScheduleUnloadFailure : DoctorDetailFailure()
}
