package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConsultationDetail(
    val id : String?,
    val orderCode : String?,
    val refferenceAppointmentId : String?,
    val status: String?,
    val statusDetail: AppointmentStatusDetail?,
    val symtomNote: String?,
    val schedule : DoctorSchedule?,
    val doctor : ConsultationDoctor?,
    val user : ConsultationUser?,
    val patient : PatientCosulDetail?,
    val totalPrice : Int?,
    val transactionConsulDetail : TransactionConsulDetail?,
    val medicalResume : ConsulMedicalResume?,
    val medicalDocument : List<ConsultationMedicalDocument>?,
    val fees : List<ConsultationFee>?,
    val consultationMethod: String?,
    val patients: AppointmentPatient,
    val invoice: Invoice?
): Parcelable {

    val doctorId: String
        get() = doctor?.id ?: ""

    val doctorName: String
        get() = doctor?.name ?: ""

    val doctorSpecialist: String
        get() = doctor?.specialist?.name ?: ""

    val hospitalId: String
        get() = doctor?.hospital?.id ?: ""

    val hospitalName: String
        get() = doctor?.hospital?.name ?: ""

}