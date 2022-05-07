package id.altea.care.view.specialistsearch

import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.ChoosingDayPayloadBuilder
import id.altea.care.core.analytics.payload.SpecialistCategoryPayloadBuilder
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Doctor
import id.altea.care.core.domain.model.HospitalResult
import id.altea.care.core.domain.model.Meta
import id.altea.care.core.domain.model.Specialization
import id.altea.care.core.domain.usecase.GetDoctorSpecialistUseCase
import id.altea.care.core.domain.usecase.GetHospitalUseCase
import id.altea.care.core.domain.usecase.GetSpecialistUseCase
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getDateFromLong
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.ConstantQueryParam
import id.altea.care.view.specialistsearch.model.*
import id.altea.care.view.specialistsearch.model.SpecialistFilterPriceModel.*
import id.altea.care.view.specialistsearch.model.SpecialistFilterPriceModel.FilterPriceType.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import java.util.*
import java.util.logging.Handler
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by trileksono on 02/03/21.
 */
class SpecialistSearchVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getDoctorSpecialistUseCase: GetDoctorSpecialistUseCase,
    private val getSpecialistUseCase: GetSpecialistUseCase,
    private val getHospitalUseCase: GetHospitalUseCase,
    private val analyticManager: AnalyticManager
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    private val bodyParam = HashMap<String, Any>()

    val sortList = ArrayList<SpecialistSortModel>()
    val loadingLoadMoreEvent = SingleLiveEvent<Boolean>()
    val filterSpecialistEvent = SingleLiveEvent<MutableList<SpecialistFilterSpecializationModel>>()
    val filterHospitalEvent = SingleLiveEvent<MutableList<SpecialistFilterHospitalModel>>()
    val filterPriceEvent = SingleLiveEvent<MutableList<SpecialistFilterPriceModel>>()
    val filterDateEvent = SingleLiveEvent<MutableList<SpecialistFilterDayModel>>()
    val filterSpecialist :  MutableList<SpecialistFilterSpecializationModel>? = null
    val filterHospital : MutableList<SpecialistFilterHospitalModel>? = null
    val filterSpecialistAndHospitalEvent = SingleLiveEvent<Pair<MutableList<SpecialistFilterSpecializationModel>?,MutableList<SpecialistFilterHospitalModel>?>>()

    val filterActive = mutableListOf<FilterActiveModel>()

    val specialistDoctor = SingleLiveEvent<Pair<List<Doctor>, Meta>>()

    init {
        getSpecialistAndHospital()
        filterPriceEvent.value = arrayListOf(
            SpecialistFilterPriceModel(
                false,
                "< 150 Rb",
                LowerThan150
            ),
            SpecialistFilterPriceModel(
                false,
                "150 - 300 Rb",
                Range150And300
            ),
            SpecialistFilterPriceModel(
                false,
                "> 300 Rb",
                GreaterThan300
            )
        )
        generateOneWeek()
    }

    fun fetchApiDoctorSpecialist() {
        executeJob {
            getDoctorSpecialistUseCase.getDoctorSpecialist(bodyParam)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe(
                    {
                        specialistDoctor.value = it.data.orEmpty() to it.meta
                    },
                    {
                        if (it is HttpException && it.code() == 404) {
                            handleFailure(NotFoundFailure.DataNotExist())
                            specialistDoctor.value = listOf<Doctor>() to Meta(0, 0, 0)
                            return@subscribe
                        }
                        handleFailure(Failure.ServerError(it.message ?: ""))
                    }
                ).disposedBy(disposable)
        }
    }

    fun setQueryParamDoctorSpecialist(query: String?, idSpecilist: String?) {
        if (query.isNullOrEmpty()) {
            bodyParam.remove(ConstantQueryParam.QUERY_SEARCH)
            bodyParam[ConstantQueryParam.QUERY_SPECIALIST] = idSpecilist ?: ""
        } else {
            bodyParam[ConstantQueryParam.QUERY_SEARCH] = query
            bodyParam.remove(ConstantQueryParam.QUERY_SPECIALIST)
        }
        fetchApiDoctorSpecialist()
    }

    private fun getHospital() {
        executeJob {
            getHospitalUseCase.getHospital()
                .compose(applySchedulers())
                .subscribe(
                    {
                        filterHospitalEvent.value = ArrayList(
                            it.map { HospitalResult.mapToHospitalFilterModel(it) }
                        )
                    },
                    { Timber.e(it.localizedMessage) })
                .disposedBy(disposable)
        }
    }

    private fun getSpecialist() {
        executeJob {
            getSpecialistUseCase.getSpecialist()
                .compose(applySchedulers())
                .subscribe(
                    { listSpecialization ->
                        filterSpecialistEvent.value = ArrayList(
                            listSpecialization.map { specialization ->
                                Specialization.toSpecializationFilterModel(specialization)
                            }
                        )


                    },
                    { Timber.e(it.localizedMessage) })
                .disposedBy(disposable)
        }
    }

    fun getSpecialistAndHospital(){
        val listSpecialist = ArrayList<Specialization>()
        val listHospital = ArrayList<HospitalResult>()
        executeJob {
            Single.merge(
                getSpecialistUseCase.getSpecialist(),
                getHospitalUseCase.getHospital()
            ).subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.forEach {data ->
                        when(data){
                            is Specialization ->{
                              listSpecialist.add(data)
                            }
                            is HospitalResult ->{
                                listHospital.add(data)
                            }
                        }
                    }

                  val specialist = ArrayList(
                        listSpecialist.distinct().map { specialization ->
                            Specialization.toSpecializationFilterModel(specialization)
                        }
                  )


                 val hopital =  ArrayList(
                        listHospital.distinct().map { HospitalResult.mapToHospitalFilterModel(it) }
                    )

                    filterSpecialistEvent.value =  ArrayList(
                        listSpecialist.distinct().map { specialization ->
                            Specialization.toSpecializationFilterModel(specialization)
                        }
                    )
                    filterHospitalEvent.value = ArrayList(
                        listHospital.distinct().map { HospitalResult.mapToHospitalFilterModel(it) }
                    )

                    filterSpecialistAndHospitalEvent.value = specialist to hopital

                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)


        }

    }

    fun onFirstLaunch(specialistId: String?, query: String?,specialistIds: Array<String>?, hospitalIds: Array<String>?) {
        if (specialistId?.isNotEmpty() == true){
            filterActive.apply {
                specialistId?.let { add(FilterActiveModel.FilterSpecialist(it, "")) }
            }
        }else{
            filterActive.apply {
                specialistIds?.forEach {
                    add(FilterActiveModel.FilterSpecialist(it,""))
                }
            }
            filterActive.apply {
                hospitalIds?.forEach {
                    add(FilterActiveModel.FilterHospital(it,""))
                }
            }
        }

        bodyParam.apply {
            clear()
            put(ConstantQueryParam.QUERY_SPECIALIST, filterActive
                .filterIsInstance<FilterActiveModel.FilterSpecialist>()
                .map { it.idSpecialis })

            put(ConstantQueryParam.QUERY_HOSPITAL,filterActive.filterIsInstance<FilterActiveModel.FilterHospital>().map {
                it.idHospital
            })

            put(ConstantQueryParam.QUERY_AVAILABLE_DAY, filterActive
                .filterIsInstance<FilterActiveModel.FilterDate>()
                .map { filterDate ->
                    sendTrackingEventChoosingDayToAnalytics(filterDate.dayServer)
                    filterDate.dayServer
                })

            query?.let {
                if (it.isNotEmpty()) {
                    put(ConstantQueryParam.QUERY_SEARCH, it)
                }
            }

        }
        addSortBodyParam()
        fetchApiDoctorSpecialist()

    }

    fun reselectSortModel(sortModel: SpecialistSortModel) {
        sortList.listIterator().let {
            while (it.hasNext()) {
                val old = it.next()
                if (old.title != sortModel.title) {
                    it.set(old.apply { isSelected = false })
                } else {
                    it.set(old.apply { isSelected = true })
                }
            }
        }
        addSortBodyParam()
        fetchApiDoctorSpecialist()
    }

    fun onFilterDayChange(day: SpecialistFilterDayModel) {
        val findFilter = filterActive.withIndex().find { it.value is FilterActiveModel.FilterDate }
        filterActive.apply {
            findFilter?.index?.let { filterActive.removeAt(it) }
            filterActive.add(FilterActiveModel.FilterDate(day.dayLocale, day.dayServer, day.date))
        }

        bodyParam[ConstantQueryParam.QUERY_AVAILABLE_DAY] =
            filterActive.filterIsInstance<FilterActiveModel.FilterDate>().map { it.dayServer }
        fetchApiDoctorSpecialist()
        sendTrackingEventChoosingDayToAnalytics(day.dayServer)
    }

    fun doAfterFilter(query : String?,filter: MutableList<FilterActiveModel>) {
        filterActive.clear()
        bodyParam.clear()
        filterActive.addAll(filter)

        if (query?.isNotEmpty() == true){
            bodyParam[ConstantQueryParam.QUERY_SEARCH] = query
            bodyParam.remove(ConstantQueryParam.QUERY_SPECIALIST)
        }else{
            bodyParam.remove(ConstantQueryParam.QUERY_SEARCH)
        }
        generateBodyParam()
    }

    fun doAfterRemoveFilter(query: String?,filter: FilterActiveModel) {
        val index = filterActive.withIndex().firstOrNull { it.value.view == filter.view }
        index?.let { filterActive.removeAt(it.index) }
        bodyParam.clear()
        if (query?.isNotEmpty() == true){
            bodyParam[ConstantQueryParam.QUERY_SEARCH] = query
            bodyParam.remove(ConstantQueryParam.QUERY_SPECIALIST)
        }else{
            bodyParam.remove(ConstantQueryParam.QUERY_SEARCH)
        }
        generateBodyParam()
    }

    private fun generateBodyParam() {
        val specialistIdFilter = mutableListOf<String>()
        val hospitalIdFilter = mutableListOf<String>()

        filterActive.forEach {
            when (it) {
                is FilterActiveModel.FilterSpecialist -> specialistIdFilter.add(it.idSpecialis)
                is FilterActiveModel.FilterHospital -> hospitalIdFilter.add(it.idHospital)
                is FilterActiveModel.FilterPrice -> {
                    when (it.priceType) {
                        is GreaterThan300 -> bodyParam[ConstantQueryParam.QUERY_PRICE_GT] = 300000
                        is LowerThan150 -> bodyParam[ConstantQueryParam.QUERY_PRICE_LT] = 150000
                        is Range150And300 -> {
                            bodyParam[ConstantQueryParam.QUERY_PRICE_LTE] = 300000
                            bodyParam[ConstantQueryParam.QUERY_PRICE_GTE] = 150000
                        }
                    }
                }
                is FilterActiveModel.FilterDate -> {
                    bodyParam[ConstantQueryParam.QUERY_AVAILABLE_DAY] = it.dayServer
                }
            }
        }

        if (specialistIdFilter.isNotEmpty()) {
            bodyParam[ConstantQueryParam.QUERY_SPECIALIST] = specialistIdFilter
        }

        if (hospitalIdFilter.isNotEmpty()) {
            bodyParam[ConstantQueryParam.QUERY_HOSPITAL] = hospitalIdFilter
        }

        fetchApiDoctorSpecialist()
    }

    private fun addSortBodyParam() {
        // add sort to parameter
        sortList.find { it.isSelected }?.let {
            bodyParam.put(
                it.queryParam.split("=")[0], it.queryParam.split("=")[1]
            )
        }
    }

    private fun generateOneWeek() {
        val calendar = Calendar.getInstance()
        val listDate = mutableListOf<SpecialistFilterDayModel>()
        for (i in 0 until 7) {
            val localDay = calendar.timeInMillis.getDateFromLong("EEEE", true)
            val serverDay =
                calendar.timeInMillis.getDateFromLong("EEEE", false).toUpperCase(Locale.ROOT)
            val date = calendar.timeInMillis.getDateFromLong("yyyy-MM-dd")
            listDate.add(
                SpecialistFilterDayModel(
                    if (i == 0) "Hari ini" else localDay,
                    serverDay,
                    date,
                    i == 0
                )
            )

            if (i == 0) {
                filterActive.add(
                    FilterActiveModel.FilterDate("Hari ini", serverDay, date)
                )
            }

            calendar.add(Calendar.DATE, 1)
        }

        filterDateEvent.value = listDate
    }

    private fun sendTrackingEventChoosingDayToAnalytics(day: String) {
        analyticManager.trackingEventChoosingDay(ChoosingDayPayloadBuilder(day))
    }

}