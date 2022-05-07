package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyScheduleDummy(
    val orderId: String,
    val doctorName: String,
    val doctorTitle: String,
    val hospitalName: String,
    val scheduleDate: String,
    val scheduleTime: String,
    val doctorImage: String,
    val hospitalImage: String = ""
) : Parcelable