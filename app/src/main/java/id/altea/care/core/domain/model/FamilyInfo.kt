package id.altea.care.core.domain.model

import android.os.Parcelable
import id.altea.care.view.common.enums.Gender
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FamilyInfo(val family_relation_type : String?,val contactFamily : String?, val firstName : String?,
                      val lastName : String?,val cardId : String?,
                      val gender : Gender?,val birthDate : String?,
                      val birthPlace : String?,val birthCountryId : String?,val birthCountry : String?,
                      val nationalityId : String?,val nationality : String?,val addressId : String?,var email : String?,var phone : String?) : Parcelable