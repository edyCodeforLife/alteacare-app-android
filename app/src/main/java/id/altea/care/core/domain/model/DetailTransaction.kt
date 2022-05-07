package id.altea.care.core.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailTransaction(
    val code: String?,
    val description: String?,
    val icon: String?,
    val name: String?,
    val provider: String?
) : Parcelable