package id.altea.care.core.ext

import android.content.Context
import id.altea.care.R
import id.altea.care.core.domain.model.UserAddress

fun UserAddress?.getCompleteAddress(context: Context): String {
    return if (this != null) {
        String.format(context.getString(R.string.full_address_s),
            street ?: "-",
            rtRw ?: "-",
            subDistrict?.name ?: "-",
            district?.name ?: "-",
            "${city?.name} ${province?.name} ${subDistrict?.postalCode}"
        )
    } else {
        "-"
    }
}

fun String.addStringAtIndex(text: String, index: Int) =
    StringBuilder(this).apply { insert(index, text) }.toString()
