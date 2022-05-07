package id.altea.care.core.domain.model

import android.os.Parcelable

abstract class SelectedModel : Parcelable {
    var isChecked = false
    abstract fun getIdModel(): String
    abstract fun getTitle(): String
}
