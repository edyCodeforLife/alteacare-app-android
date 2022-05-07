package id.altea.care.view.generalsearch.model

import android.os.Parcelable
import id.altea.care.view.generalsearch.item.TypeContent
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchContent(val id: String, val content: String, val typeContent: TypeContent, val icon : String? = null) :Parcelable
