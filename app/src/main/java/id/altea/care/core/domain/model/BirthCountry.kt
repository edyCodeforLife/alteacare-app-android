package id.altea.care.core.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BirthCountry (
        val code: String?,
        val id: String?,
        val name: String?
) : Parcelable