package id.altea.care.core.data.repositoryimpl

import id.altea.care.core.data.network.api.DataApi
import id.altea.care.core.data.request.MessageRequest
import id.altea.care.core.data.response.*
import id.altea.care.core.data.response.OperationalHourMAResponse.Companion.mapToOperationalHourMA
import id.altea.care.core.data.response.SymptomsResponse.Companion.toSymptoms
import id.altea.care.core.domain.model.*
import id.altea.care.core.domain.model.City
import id.altea.care.core.domain.model.FormatImage
import id.altea.care.core.domain.model.Province
import id.altea.care.core.domain.model.SubDistrict
import id.altea.care.core.domain.repository.DataRepository
import id.altea.care.core.helper.ProxyRetrofitQueryMap
import id.altea.care.core.mappers.Mapper.mapToPromotionList
import id.altea.care.core.mappers.Mapper.mapToPromotionListGroup
import id.altea.care.core.mappers.Mapper.mapToWidgets
import io.reactivex.Single
import okhttp3.MultipartBody

class DataRepositoryImpl(private val dataApi: DataApi) : DataRepository {
    override fun uploadFiles(photoPart: MultipartBody.Part): Single<Result<Files>> {
        return dataApi.uploadFiles(photoPart).map { response ->
            Result(
                status = response.status,
                message = response.message,
                data = Files(
                    id = response.data.id,
                    name = response.data.name,
                    url = response.data.url,
                    size = response.data.size ?: 0.0,
                    formats = FormatImage(
                        thumbnail = response.data.formats.thumbnail,
                        large = response.data.formats.large,
                        medium = response.data.formats.medium,
                        small = response.data.formats.small
                    )
                )
            )
        }

    }

    override fun generalSearch(
        queryParam: Map<String, String>
    ): Single<GeneralSearch> {
        return dataApi.getGeneralSearch(queryParam)
            .map { generalSearch ->
                generalSearch.data.toGeneralSearch(generalSearch.metaResponse)
            }
    }

    override fun getSpecialist(queryParam: Map<String, String>): Single<List<Specialization>> {
        return dataApi.getSpecialist(queryParam)
            .map { response ->
                response.data.map { specializationResponse -> specializationResponse.toSpecializationModel() }
            }
    }

    override fun getDoctorDetail(
        doctorId: String
    ): Single<Doctor> {
        return dataApi.getDoctorDetail(doctorId)
            .map { response ->
                response.data.toDoctor()
            }
    }

    override fun getDoctorSpecialistFilter(
        queryParam: Map<String, Any>
    ): Single<ResultMeta<List<Doctor>>> {
        return dataApi.getDoctorSpecialistFilter(
            ProxyRetrofitQueryMap(queryParam)
        ).map { response ->
            ResultMeta(
                MetaResponse.toMeta(response.metaResponse),
                response.status,
                response.message,
                response.data.map { doctorResponse -> doctorResponse.toDoctor() })
        }
    }

    override fun getCountries(queryParam: Map<String, String>): Single<List<Country>> {
        return dataApi.getCountries(queryParam)
            .map { response ->
                response.data.map { countryResponse -> countryResponse.toCountry() }
            }
    }

    override fun getDoctorSchedule(doctorId: String, date: String): Single<List<DoctorSchedule>> {
        return dataApi.getDoctorSchedule(doctorId, date)
            .map { response ->
                response.data.map { doctorScheduleResponse ->
                    DoctorScheduleResponse.toDoctorSchedule(
                        doctorScheduleResponse
                    )
                }
            }
    }

    override fun getPaymentTypes(queryParam: Map<String, Any>): Single<List<PaymentTypes>> {
        return dataApi.getPaymentType(queryParam)
            .map { response ->
                response.data.map { paymentTypesResponse ->
                    PaymentTypesResponse.mapToPaymentTypes(
                        paymentTypesResponse
                    )
                }
            }
    }

    override fun getBlocks(queryParam: Map<String, String>): Single<List<Block>> {
        return dataApi.getBlocks(queryParam)
            .map { response ->
                response.data.map { blockResponse -> blockResponse.toBlock() }
            }
    }

    override fun getInformationCentral(): Single<InformationCentral> {
        return dataApi.getInformationCentral()
            .map { response ->
                response.data.toInformationCentral()
            }
    }

    override fun getMessageType(): Single<List<TypeMessage>> {
        return dataApi.getMessageType()
            .map { response ->
                response.data.map { typeMessageResponse -> typeMessageResponse.toTypeMessage() }
            }
    }

    override fun sendMessage(messageRequest: MessageRequest): Single<Boolean> {
        return dataApi.sendMessage(messageRequest).map { response ->
            response.status
        }
    }

    override fun getBanner(category: String): Single<List<Banner>> {
        return dataApi.getBanner(Category = category)
            .map { response ->
                response.data.map { bannerResponse -> bannerResponse.toBanner() }
            }
    }

    override fun getContactFamily(): Single<List<FamilyContact>> {
        return dataApi.getContactFamily()
            .map { response ->
                response.data.map { familyContactResponse -> familyContactResponse.toFamilyContact() }
            }
    }

    override fun searchHospital(queryParam: Map<String, String>): Single<List<HospitalResult>> {
        return dataApi.searchHospital(queryParam)
            .map { response ->
                response.data.map { hospitalResponse -> hospitalResponse.toHospital() }
            }
    }

    override fun getFaq(): Single<List<Faq>> {
        return dataApi.getFaq()
            .map { response ->
                response.data.map { faqResponse -> faqResponse.toFaq() }
            }
    }

    override fun getProvince(countryId: String): Single<List<Province>> {
        return dataApi.getProvince(countryId)
            .map { response ->
                response.data.map { provinceResponse ->
                    ProvinceResponse.mapToModel(
                        provinceResponse
                    )
                }
            }
    }

    override fun getCity(provinceId: String): Single<List<City>> {
        return dataApi.getCity(provinceId).map { response ->
            response.data.map { cityResponse ->
                CityResponse.mapToModel(cityResponse)
            }
        }
    }

    override fun getDistrict(cityId: String): Single<List<District>> {
        return dataApi.getDistrict(cityId).map { response ->
            response.data.map { districtResponse ->
                DistrictResponse.mapToModel(districtResponse)
            }
        }
    }

    override fun getSubDistrict(districtId: String): Single<List<SubDistrict>> {
        return dataApi.getSubDistrict(districtId).map { response ->
            response.data.map { subDistrictResponse ->
                SubDistrictResponse.mapToModel(
                    subDistrictResponse
                )
            }
        }
    }

    override fun getVersionApp(): Single<CheckApp> {
        return dataApi.getVersionApp().map { response ->
            response.data.toCheckApp()
        }
    }

    override fun geySymptoms(query: String, page: Int): Single<List<Symptoms>> {
        return dataApi.getSymptoms(query, page)
            .map { response ->
                response.data.map { it.toSymptoms() }
            }
    }

    override fun getOperationalHourMA(): Single<OperationalHourMA> {
        return dataApi.operationalHourMA().map { response ->
            response.data.mapToOperationalHourMA()
        }
    }

    override fun getWidgets(): Single<List<Widgets>> {
        return dataApi.getWidgets()
            .map { response ->
                response.data.map { widgetsResponse ->
                    widgetsResponse.mapToWidgets()
                }
            }
    }


}
