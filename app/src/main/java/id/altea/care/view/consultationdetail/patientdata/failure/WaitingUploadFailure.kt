package id.altea.care.view.consultationdetail.patientdata.failure

import id.altea.care.core.exception.Failure

sealed class FileProcessFailure : Failure.FeatureFailure() {
    object WaitingUploadFailure : FileProcessFailure()
}
