package id.altea.care.view.generalsearch

import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.SearchResultPayloadBuilder
import id.altea.care.core.analytics.payload.SpecialistCategoryPayloadBuilder
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.GeneralSearch
import id.altea.care.core.domain.model.Specialization
import id.altea.care.core.domain.model.Symptoms
import id.altea.care.core.domain.usecase.GetGeneralSearch
import id.altea.care.core.domain.usecase.GetSpecialistUseCase
import id.altea.care.core.domain.usecase.GetSymptomsUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.ConstantQueryParam
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class GeneralSearchVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getGeneralSearch: GetGeneralSearch,
    private val analyticManager: AnalyticManager,
    private val getSymptomsUseCase: GetSymptomsUseCase,
    private val getSpecialistUseCase: GetSpecialistUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val generalSearchResult = SingleLiveEvent<GeneralSearch>()
    val getSymptomsEvent = SingleLiveEvent<List<Symptoms>>()
    val getSpecialist = SingleLiveEvent<List<Specialization>>()

    val isNextPageAvailableEvent = SingleLiveEvent<Boolean>()

    fun getGeneralSearch(query: String) {
        fetchApi(mapOf(Pair(ConstantQueryParam.QUERY_SEARCH, query)))
    }

    fun getPopularSearch() {
        fetchApi(mapOf(Pair(ConstantQueryParam.QUERY_IS_POPULAR, "YES")))
    }

    private fun fetchApi(queryParam: Map<String, String>) {
        executeJob {
            getGeneralSearch.doSearching(queryParam)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe(
                    { generalSearchResult.postValue(it) },
                    { handleFailure(it.getGeneralErrorServer()) }
                )
                .disposedBy(disposable)
        }
    }

    fun sendEventSearchResultToAnalytics(
        specialistCategory: String?,
        specialistDoctorName: String?,
        symptom: String?
    ) {
        analyticManager.trackingEventSearchResult(
            SearchResultPayloadBuilder(
                specialistCategory ?: "",
                specialistDoctorName ?: "",
                symptom
            )
        )
    }

    fun getSymptomsList(
        query: String,
        page: Int
    ) {
        executeJob {
            getSymptomsUseCase.getSymptoms(query, page)
                .compose(applySchedulers())
                .subscribe({ listSymptom ->
                    getSymptomsEvent.value = listSymptom
                    isNextPageAvailableEvent.value = listSymptom.isNotEmpty()
                }, { throwable ->
                    isNextPageAvailableEvent.value = false
                    throwable.printStackTrace()
                    handleFailure(throwable.getGeneralErrorServer())
                })
                .disposedBy(disposable)
        }
    }

    fun getSpecialistCategory(query: String) {
        executeJob {
            val queryParam = mapOf(Pair(ConstantQueryParam.QUERY_SEARCH, query))
            getSpecialistUseCase.searchSpecialist(queryParam)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe({ specialistList ->
                    getSpecialist.value = specialistList
                }, { throwable ->
                    throwable.printStackTrace()
                    handleFailure(throwable.getGeneralErrorServer())
                })
                .disposedBy(disposable)
        }
    }

    fun sendEventSpecialistCategoryToAnalytics(
        specialistCategoryId: String?,
        specialistCategoryName: String?
    ) {
        analyticManager.trackingEventSpecialistCategory(
            SpecialistCategoryPayloadBuilder(
                specialistCategoryId,
                specialistCategoryName
            )
        )
    }
}