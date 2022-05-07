package id.altea.care.core.domain.repository

import id.altea.care.core.data.request.*
import id.altea.care.core.data.response.*
import id.altea.care.core.domain.model.*
import io.reactivex.Completable
import io.reactivex.Single

interface UserRepository {

    fun getProfile(): Single<Response<ProfileResponse>>

    fun getProfileFromRegister(token: String): Single<Response<ProfileResponse>>

    fun login(loginRequest: LoginRequest): Single<Auth>

    fun logout() : Single<General>

    fun doRegister(payload: RegisterRequest): Single<Auth>

    fun requestEmailOtpRegister(email: EmailRequest, token: String): Single<Boolean>

    fun requestValidateOtpRegister(
        otpValidation: EmailOtpValidationRequest,
        token: String
    ): Single<Auth>

    fun requestChangeEmailRegister(email: EmailRequest, token: String): Single<Boolean>


    fun requestEmailOrSmsOtpForgotPassword(username: ForgotPasswordRequest, token: String? = null): Single<ForgotPassword>

    fun validationEmailOtpForgotPassword(
        otpValidation: PasswordVerifyRequest,
        token: String? = null
    ): Single<Auth>

    fun changePassword(changePasswordRequest: ChangePasswordRequest): Single<Boolean>

    fun updateAvatar(avatar : UpdateAvatarRequest) : Single<General>

    fun requestChangeEmail(params : EmailRequest) : Single<Boolean>

    fun changeEmailOtp(params : EmailOtpValidationRequest) : Single<Boolean>

    fun requestChangePhoneNumber(params : PhoneNumberRequest) : Single<Boolean>

    fun changePhoneNumberOtp(params : PhoneNumberOtpValidationRequest) : Single<Boolean>

    fun requestCheckPassword(params : PasswordRequest) : Single<Boolean>

    fun deteleAvatar() : Single<Boolean>

    fun changeForgotPassword(token : String, params: ChangePasswordRequest): Single<Boolean>

    fun getProfileAddress(page: Int): Single<PageAddress>

    fun setPrimaryAddress(idAddress: String): Completable

    fun deleteAddress(idAddress: String): Completable

    fun addFamilyMember(params : AddFamilyRequest) : Single<Boolean>

    fun addMemberFamilyRegisterAccount(params : AddMemberFamilyRegisterAccountRequest) : Single<Boolean>

    fun addMemberFamilyExisitingAccount(patientId : String,addFamilyExistingAccountRequest : AddFamilyExistingAccountRequest) : Completable

    fun updateFamilyMember(params : AddFamilyRequest, patientId : String) : Single<Boolean>

    fun getFamilyMember(page : Int) : Single<PagePatient>

    fun createAddress(addressRequest: AddressRequest): Completable

    fun updateAddress(idAddress: String, addressRequest: AddressRequest): Completable

    fun getFamilyDetailMember(patientId : String) : Single<DetailPatient>

    fun deleteFamilyMember(patientId: String) : Single<Boolean>

    fun setPrimaryFamily(patientId : String) : Completable

    fun checkUser(params: AddCheckUserRequest): Single<CheckUser>

    fun doRequestSmsOtpRegistration(params: SmsRequest, token: String): Single<Boolean>

    fun doValidationSmsOtpRegistration(params: SmsOtpValidationRequest, token: String): Single<Auth>

    fun doChangePhoneNumberRegister(params: SmsRequest, token: String): Single<Boolean>

}
