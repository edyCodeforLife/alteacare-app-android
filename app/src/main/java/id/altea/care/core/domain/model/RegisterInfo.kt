package id.altea.care.core.domain.model

import android.os.Parcelable
import id.altea.care.view.common.enums.Gender
import kotlinx.android.parcel.Parcelize

/**
 * Created by trileksono on 05/03/21.
 */
@Parcelize
class RegisterInfo(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val gender: Gender? = null,
    val resultFilter : ResultFilter?=null,
    val cityOfBirth : String?=null,
    var phoneNumber: String? = null,
    var email: String? = null,
    var password: String? = null,
    var passwordConfirm: String? = null
) : Parcelable {

    val newPhoneNumber: String
        get() = "0$phoneNumber"

}
