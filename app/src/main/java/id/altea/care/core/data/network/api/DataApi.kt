package id.altea.care.core.data.network.api

import id.altea.care.core.data.request.MessageRequest
import id.altea.care.core.data.response.*
import id.altea.care.core.helper.ProxyRetrofitQueryMap
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface DataApi {

    @Multipart
    @POST("/file/v1/file/upload")
    fun uploadFiles(@Part photoPart: MultipartBody.Part): Single<Response<DataResponseUpload>>

    @GET("/data/doctors")
    fun getDoctorSpecialistFilter(
        @QueryMap queryParam: ProxyRetrofitQueryMap
    ): Single<MetaBaseResponse<List<DoctorResponse>>>

    @GET("/data/doctors/{doctorId}")
    fun getDoctorDetail(
        @Path("doctorId") doctorId: String
    ): Single<Response<DoctorResponse>>

    @GET("/data/search")
    fun getGeneralSearch(
        @QueryMap queryParam: Map<String, String>
    ): Single<MetaBaseResponseGeneralSearch<GeneralSearchResponse>>

    @GET("/data/specializations")
    fun getSpecialist(@QueryMap queryParam: Map<String, String>): Single<Response<List<SpecializationResponse>>>

    @GET("/data/hospitals")
    fun searchHospital(@QueryMap queryParam: Map<String, String>) : Single<Response<List<HospitalDomisiliResponse>>>

    @GET("/data/doctor-schedules")
    fun getDoctorSchedule(
        @Query("doctor_id") doctorId: String,
        @Query("date") date: String //yyyy-MM-dd
    ): Single<Response<List<DoctorScheduleResponse>>>

    @GET("/data/faqs")
    fun getFaq() : Single<Response<List<FaqResponse>>>


    @GET("/data/countries")
    fun getCountries(@QueryMap queryParam: Map<String, String>) : Single<Response<List<CountryResponse>>>

    @GET("/data/payment-types")
    fun getPaymentType(
        @QueryMap queryParam: Map<String, @JvmSuppressWildcards Any>
    ): Single<Response<List<PaymentTypesResponse>>>

    @GET("/data/blocks")
    fun getBlocks(@QueryMap queryParam: Map<String, String> = mapOf()) : Single<Response<List<BlockResponse>>>

    @GET("/data/contents/PUSAT_INFORMASI")
    fun getInformationCentral() : Single<Response<InformationCentralResponse>>

    @GET("/data/message-types")
    fun getMessageType() : Single<Response<List<TypeMessageResponse>>>

    @POST("/data/messages")
    fun sendMessage(
        @Body messageRequest: MessageRequest) : Single<Response<Any>>

    @GET("/data/banners")
    fun getBanner(
        @Query("category") Category : String
    ) : Single<Response<List<BannerResponse>>>

    @GET("/data/provinces")
    fun getProvince(@Query("country") countryId: String): Single<Response<List<ProvinceResponse>>>

    @GET("/data/cities")
    fun getCity(@Query("province") provinceId: String): Single<Response<List<CityResponse>>>

    @GET("/data/districts")
    fun getDistrict(@Query("city") cityId: String): Single<Response<List<DistrictResponse>>>

    @GET("/data/sub-districts")
    fun getSubDistrict(@Query("district") districtId: String): Single<Response<List<SubDistrictResponse>>>

    @GET("/data/family-relation-types")
    fun getContactFamily() : Single<Response<List<FamilyContactResponse>>>

    @GET("/data/version-applications")
    fun getVersionApp() : Single<Response<CheckAppResponse>>

    @GET("/data/symtoms")
    fun getSymptoms(
        @Query("_q") query : String,
        @Query("_page") page : Int
    ) : Single<Response<List<SymptomsResponse>>>

    @GET("data/setting")
    fun operationalHourMA() : Single<Response<OperationalHourMAResponse>>

    @GET("data/widgets")
    fun getWidgets() : Single<Response<List<WidgetsResponse>>>
}
