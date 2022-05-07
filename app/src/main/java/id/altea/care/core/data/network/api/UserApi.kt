package id.altea.care.core.data.network.api

import id.altea.care.core.data.request.*
import id.altea.care.core.data.response.*
import io.reactivex.Single
import retrofit2.http.*

interface UserApi {

    @POST("/user/auth/login")
    fun login(@Body login: LoginRequest): Single<Response<AuthResponse>>

    @POST("/user/auth/register")
    fun doRequestRegister(@Body params: RegisterRequest): Single<Response<AuthResponse>>

    @POST("/user/email/verification")
    fun doRequestEmailOtpRegistration(
        @Header("Authorization") token: String,
        @Body params: EmailRequest
    ): Single<Response<Any>>

    @POST("/user/email/verify")
    fun doValidationEmailOtpRegistration(
        @Header("Authorization") token: String,
        @Body params: EmailOtpValidationRequest
    ): Single<Response<AuthResponse>>

    @POST("/user/email/change/register")
    fun requestChangeEmailRegister(
        @Header("Authorization") token: String,
        @Body params: EmailRequest
    ): Single<Response<Any>>


    @POST("/user/password/forgot")
    fun requestEmailOrSmsOtpForgotPassword(
        @Header("Authorization") token: String,
        @Body params: ForgotPasswordRequest
    ): Single<Response<ForgotPasswordResponse>>

    @POST("/user/password/verify")
    fun validationEmailOtpForgotPassword(
        @Header("Authorization") token: String,
        @Body params: PasswordVerifyRequest
    ): Single<Response<AuthResponse>>

    @POST("/user/password/change")
    fun changePassword(
        @Body params: ChangePasswordRequest
    ): Single<Response<Any>>

    @POST("/user/password/change")
    fun changeForgotPassword(
        @Header("Authorization") token : String,
        @Body params: ChangePasswordRequest
    ): Single<Response<Any>>


    @POST("/user/auth/logout")
    fun logout() : Single<GeneralResponse>

    @POST("/user/profile/update-avatar")
    fun updateAvatar(@Body params : UpdateAvatarRequest) : Single<GeneralResponse>

    @DELETE("/user/profile/update-avatar")
    fun deteleAvatar() : Single<Response<Any>>

    @GET("/user/profile/me")
    fun getProfile(): Single<Response<ProfileResponse>>

    @GET("/user/profile/me")
    fun getProfileFromRegister(
        @Header("Authorization") token : String,
        ): Single<Response<ProfileResponse>>

    @POST("/user/email/change/otp")
    fun requestChangeEmail(@Body params : EmailRequest) : Single<Response<Any>>

    @POST("/user/email/change")
    fun changeEmailOtp(@Body params : EmailOtpValidationRequest) : Single<Response<Any>>

    @POST("/user/phone/change/otp")
    fun requestChangePhoneNumber(@Body params : PhoneNumberRequest) : Single<Response<Any>>

    @POST("/user/phone/change")
    fun changePhoneNumberOtp(@Body params : PhoneNumberOtpValidationRequest) : Single<Response<Any>>

    @POST("/user/password/check")
    fun requestCheckPassword(@Body params : PasswordRequest) : Single<Response<Any>>

    @GET("/user/address")
    fun getProfileAddress(@Query("page") page: Int): Single<Response<PageAddressResponse>>

    @GET("/user/address/{id}/set-primary")
    fun setPrimaryAddress(@Path("id") id: String): Single<Any>

    @DELETE("/user/address/{addressId}")
    fun deleteAddress(@Path("addressId") addressId: String): Single<Any>

    @POST("/user/address")
    fun createAddress(@Body addressRequest: AddressRequest) : Single<Response<Any>>

    @POST("/user/address/{addressId}")
    fun updateAddress(
        @Body addressRequest: AddressRequest,
        @Path("addressId") addressId: String
    ) : Single<Response<Any>>

    @POST("/user/patient")
    fun addFamilyMember(@Body params : AddFamilyRequest) : Single<Response<Any>>

    @POST("/user/patient")
    fun addMemberFamilyRegisterAccount(@Body params : AddMemberFamilyRegisterAccountRequest) : Single<Response<Any>>

    @POST("/user/patient/{patientId}/register")
    fun addMemberFamilyExisitingAccount(@Path("patientId") patientId : String,
                                        @Body addFamilyExistingAccountRequest : AddFamilyExistingAccountRequest) : Single<Any>

    @POST("/user/patient/{patientId}")
    fun updateFamilyMember(@Body params : AddFamilyRequest,
                           @Path("patientId") patientId : String) : Single<Response<Any>>

    @GET("/user/patient")
    fun getFamilyMember(@Query("page") page : Int) : Single<Response<PagePatientResponse>>

    @GET("/user/patient/{patientId}")
    fun getFamilyDetailMember(@Path("patientId") patientId : String) : Single<Response<DetailPatientResponse>>

    @DELETE("/user/patient/{patientId}")
    fun deleteFamilyMember(@Path("patientId") patientId: String) : Single<Response<Any>>

    @GET("/user/patient/{patientId}/set-default")
    fun setPrimaryFamily(@Path("patientId") patientId : String) : Single<Any>

    @POST("/user/users/check-user")
    fun checkUser(@Body params : AddCheckUserRequest) : Single<Response<ResponseCheckUser>>

    @POST("/user/phone/verification")
    fun doRequestSmsOtpRegistration(
        @Header("Authorization") token: String,
        @Body params: SmsRequest
    ): Single<Response<AuthResponse>>

    @POST("/user/phone/verify")
    fun doValidationSmsOtpRegistration(
        @Header("Authorization") token: String,
        @Body params: SmsOtpValidationRequest
    ):  Single<Response<AuthResponse>>

    @POST("/user/phone/change/register")
    fun requestChangePhoneNumberRegister(
        @Header("Authorization") token: String,
        @Body params: SmsRequest
    ): Single<Response<Any>>

}