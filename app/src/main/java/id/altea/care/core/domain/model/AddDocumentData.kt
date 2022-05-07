package id.altea.care.core.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddDocumentData (
    val date: String?,
    val dateRaw: String?,
    val fileId: String?,
    val id: Int?,
    val originalName: String?,
    val size: String?,
    val uploadByUser: Int?,
    val url: String?
) : Parcelable