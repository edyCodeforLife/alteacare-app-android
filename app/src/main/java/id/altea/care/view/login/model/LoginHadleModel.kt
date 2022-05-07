package id.altea.care.view.login.model

import id.altea.care.core.domain.model.Profile

sealed class LoginHadleModel {

    data class EmailUnverified(val token: String, val email: String) : LoginHadleModel()
    data class PhoneUnverified(val token: String, val phone: String) : LoginHadleModel()
    data class VerifiedUser(val token: String, val profile: Profile) : LoginHadleModel()

}
