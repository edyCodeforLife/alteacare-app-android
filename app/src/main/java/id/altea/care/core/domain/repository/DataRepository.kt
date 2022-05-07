package id.altea.care.core.domain.repository

import id.altea.care.core.data.request.MessageRequest
import id.altea.care.core.domain.model.*
import io.reactivex.Single
import okhttp3.MultipartBody

interface DataRepository {

    fun uploadFiles(photoPart: MultipartBody.Part): Single<Result<Files>>

    fun generalSearch(
        queryParam: Map<String, String>
    ): Single<GeneralSearch>

    fun getSpecialist(queryParam: Map<String, String>): Single<List<Specialization>>

    fun getDoctorDetail(doctorId: String): Single<Doctor>

    fun getDoctorSpecialistFilter(
         queryParam: Map<String, Any>
    ): Single<ResultMeta<List<Doctor>>>

    fun getCountries(queryParam: Map<String, String>): Single<List<Country>>

    fun getDoctorSchedule(
        doctorId: String,
        date: String // yyyy-MM-dd
    ): Single<List<DoctorSchedule>>

    fun getFaq() : Single<List<Faq>>

    fun searchHospital(queryParam: Map<String, String>): Single<List<HospitalResult>>

    fun getPaymentTypes(queryParam: Map<String, Any>): Single<List<PaymentTypes>>

    fun getBlocks(queryParam: Map<String, String> = mapOf()) : Single<List<Block>>

    fun getInformationCentral() : Single<InformationCentral>

    fun getMessageType() : Single<List<TypeMessage>>

    fun sendMessage(messageRequest: MessageRequest) : Single<Boolean>

    fun getBanner(category : String) : Single<List<Banner>>

    fun getContactFamily() : Single<List<FamilyContact>>

    fun getProvince(countryId: String): Single<List<Province>>

    fun getCity(provinceId: String): Single<List<City>>

    fun getDistrict(cityId: String): Single<List<District>>

    fun getSubDistrict(districtId: String): Single<List<SubDistrict>>

    fun getVersionApp(): Single<CheckApp>

    fun geySymptoms(query: String,  page: Int): Single<List<Symptoms>>

    fun getOperationalHourMA() : Single<OperationalHourMA>

    fun getWidgets() : Single<List<Widgets>>

}
