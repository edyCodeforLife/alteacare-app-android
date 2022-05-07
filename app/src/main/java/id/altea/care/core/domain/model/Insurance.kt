package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Insurance(
        val insuranceCompanyId: String?,
        val insuranceCompanyName: String?,
        val insuranceNumber: String?,
        val insurancePlafonGroup: String?
) : Parcelable