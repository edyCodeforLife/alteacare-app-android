package id.altea.care.core.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TypeMessage(
    val id: String?,
    val name: String?
) : Parcelable