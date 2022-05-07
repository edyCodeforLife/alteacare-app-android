package id.altea.care.core.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Latest (
    val platform: String?,
    val version: String?,
    val versionCode: Int?
) : Parcelable