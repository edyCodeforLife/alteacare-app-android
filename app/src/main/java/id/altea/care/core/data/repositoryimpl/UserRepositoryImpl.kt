package id.altea.care.core.data.repositoryimpl

import id.altea.care.core.data.network.api.UserApi
import id.altea.care.core.data.request.*
import id.altea.care.core.data.response.*
import id.altea.care.core.domain.model.*
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Single

class UserRepositoryImpl(private val userApi: UserApi) : UserRepository {

    override fun getProfile(): Single<Response<ProfileResponse>> {
        return userApi.getProfile()
    }

    override fun getProfileFromRegister(token: String): Single<Response<ProfileResponse>> {
        return userApi.getProfileFromRegister(token)
    }

    override fun login(loginRequest: LoginRequest): Single<Auth> {
        return userApi.login(loginRequest).map { it.data.toAuth() }
    }

    override fun logout(): Single<General> {
        return userApi.logout().map { it.toGeneral() }
    }

    override fun doRegister(payload: RegisterRequest): Single<Auth> {
        return userApi.doRequestRegister(payload).map { it.data.toAuth() }
    }

    override fun requestEmailOtpRegister(email: EmailRequest, token: String): Single<Boolean> {
        return userApi.doRequestEmailOtpRegistration(token, email).map { it.status }
    }

    override fun requestValidateOtpRegister(
        otpValidation: EmailOtpValidationRequest,
        token: String
    ): Single<Auth> {
        return userApi.doValidationEmailOtpRegistration(token, otpValidation)
            .map { it.data.toAuth() }
    }

    override fun requestChangeEmailRegister(email: EmailRequest, token: String): Single<Boolean> {
        return userApi.requestChangeEmailRegister(token, email).map { it.status }
    }


    override fun requestEmailOrSmsOtpForgotPassword(
        username: ForgotPasswordRequest,
        token: String?
    ): Single<ForgotPassword> {
        return userApi.requestEmailOrSmsOtpForgotPassword(token.orEmpty(), username).map { it.data.toForgotPassword() }
    }

    override fun validationEmailOtpForgotPassword(
        otpValidation: PasswordVerifyRequest,
        token: String?
    ): Single<Auth> {
        return userApi.validationEmailOtpForgotPassword(token.orEmpty(), otpValidation)
            .map { it.data.toAuth() }
    }

    override fun changePassword(
        changePasswordRequest: ChangePasswordRequest
    ): Single<Boolean> {
        return userApi.changePassword(changePasswordRequest).map { it.status }
    }


    override fun updateAvatar(avatar: UpdateAvatarRequest): Single<General> {
        return userApi.updateAvatar(avatar).map { it.toGeneral() }
    }

    override fun requestChangeEmail(params: EmailRequest): Single<Boolean> {
        return userApi.requestChangeEmail(params).map { it.status }
    }

    override fun changeEmailOtp(params: EmailOtpValidationRequest): Single<Boolean> {
      return userApi.changeEmailOtp(params).map { it.status }
    }

    override fun requestChangePhoneNumber(params: PhoneNumberRequest): Single<Boolean> {
        return userApi.requestChangePhoneNumber(params).map { it.status }
    }

    override fun changePhoneNumberOtp(params: PhoneNumberOtpValidationRequest): Single<Boolean> {
       return userApi.changePhoneNumberOtp(params).map { it.status }
    }

    override fun requestCheckPassword(params: PasswordRequest): Single<Boolean> {
      return userApi.requestCheckPassword(params).map { it.status }
    }

    override fun deteleAvatar(): Single<Boolean> {
      return userApi.deteleAvatar().map { it.status }
    }

    override fun changeForgotPassword(
        token: String,
        params: ChangePasswordRequest
    ): Single<Boolean> {
        return userApi.changeForgotPassword(token,params).map { it.status }
    }

    override fun getProfileAddress(page: Int): Single<PageAddress> {
        return userApi.getProfileAddress(page).map { PageAddressResponse.toPageAddress(it.data) }
    }
    override fun addFamilyMember(params: AddFamilyRequest): Single<Boolean> {
        return userApi.addFamilyMember(params).map { it.status }
    }

    override fun addMemberFamilyRegisterAccount(params: AddMemberFamilyRegisterAccountRequest): Single<Boolean> {
        return userApi.addMemberFamilyRegisterAccount(params).map { it.status }
    }

    override fun addMemberFamilyExisitingAccount(patientId: String,addFamilyExistingAccountRequest : AddFamilyExistingAccountRequest): Completable {
        return userApi.addMemberFamilyExisitingAccount(patientId,addFamilyExistingAccountRequest).flatMapCompletable { Completable.complete() }
            .onErrorResumeNext { Completable.error(it) }
    }

    override fun updateFamilyMember(params: AddFamilyRequest, patientId: String): Single<Boolean> {
        return userApi.updateFamilyMember(params,patientId).map { it.status }
    }

    override fun getFamilyMember(page : Int): Single<PagePatient> {
        return userApi.getFamilyMember(page).map { PagePatientResponse.toPagePatient(it.data) }
    }

    override fun getFamilyDetailMember(patientId: String): Single<DetailPatient> {
        return userApi.getFamilyDetailMember(patientId).map { it.data.toPatientDetailResponse() }
    }

    override fun deleteFamilyMember(patientId: String): Single<Boolean> {
        return userApi.deleteFamilyMember(patientId).map { it.status }
    }

    override fun setPrimaryFamily(patientId: String): Completable {
        return userApi.setPrimaryFamily(patientId).flatMapCompletable { Completable.complete() }
                .onErrorResumeNext { Completable.error(it) }
    }

    override fun setPrimaryAddress(idAddress: String): Completable {
        return userApi.setPrimaryAddress(idAddress).flatMapCompletable { Completable.complete() }
            .onErrorResumeNext { Completable.error(it) }
    }

    override fun deleteAddress(idAddress: String): Completable {
        return userApi.deleteAddress(idAddress).flatMapCompletable { Completable.complete() }
            .onErrorResumeNext { Completable.error(it) }
    }

    override fun createAddress(addressRequest: AddressRequest): Completable {
        return userApi.createAddress(addressRequest).flatMapCompletable { Completable.complete() }
            .onErrorResumeNext { Completable.error(it) }
    }

    override fun updateAddress(idAddress: String, addressRequest: AddressRequest): Completable {
        return userApi.updateAddress(addressRequest, idAddress).flatMapCompletable { Completable.complete() }
            .onErrorResumeNext { Completable.error(it) }
    }

    override fun checkUser(params: AddCheckUserRequest): Single<CheckUser> {
        return userApi.checkUser(params).map { responseCheckUser ->
            responseCheckUser.data.mapToCheckUser() }
    }

    override fun doRequestSmsOtpRegistration(params: SmsRequest, token: String): Single<Boolean> {
        return userApi.doRequestSmsOtpRegistration(token = token, params = params).map { it.status }
    }

    override fun doValidationSmsOtpRegistration(
        params: SmsOtpValidationRequest,
        token: String
    ): Single<Auth> {
        return userApi.doValidationSmsOtpRegistration(token = token, params = params).map { it.data.toAuth() }
    }

    override fun doChangePhoneNumberRegister(params: SmsRequest, token: String): Single<Boolean> {
        return userApi.requestChangePhoneNumberRegister(token = token, params = params).map { it.status }
    }

}