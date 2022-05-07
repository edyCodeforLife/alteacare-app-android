package id.altea.care.view.specialistsearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.widget.textChanges
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Doctor
import id.altea.care.core.domain.model.Meta
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.helper.util.ConstantQueryParam
import id.altea.care.core.helper.util.ConstantQueryValue
import id.altea.care.databinding.ActivitySearchSpecialistBinding
import id.altea.care.view.common.item.ProgressBarItem
import id.altea.care.view.specialistsearch.item.*
import id.altea.care.view.specialistsearch.model.*
import kotlinx.android.synthetic.main.toolbar_search_specialist.*
import java.util.concurrent.TimeUnit

/**
 * Created by trileksono on 02/03/21.
 */
class SpecialistSearchActivity :
    BaseActivityVM<ActivitySearchSpecialistBinding, SpecialistSearchVM>() {

    private lateinit var fastAdapter: GenericFastItemAdapter
    private lateinit var itemAdapter: GenericItemAdapter

    private lateinit var filterFastAdapter: GenericFastItemAdapter

    private val viewModel by viewModels<SpecialistSearchVM> { viewModelFactory }
    private val router by lazy { SpecialistSearchActivityRouter() }
    private val specialistId by lazy { intent.getStringExtra(EXTRA_SPECIALIST_ID) }
    private val keywordParam by lazy { intent.getStringExtra(EXTRA_SPECIALIST_QUERY) }
    private val specialistIds by lazy { intent.getStringArrayExtra(EXTRA_SPECIALIST_IDS) }
    private val hospitalIds by lazy { intent.getStringArrayExtra(EXTRA_HOSPITAL_IDS) }
    private var tempSelected = mutableListOf<FilterActiveModel>()
    private var filterHospitalAndSpecilist = mutableListOf<FilterActiveModel>()
    private var tempSpecialistSelected = mutableListOf<FilterActiveModel.FilterSpecialist>()
    private var tempHospitalSelected = mutableListOf<FilterActiveModel.FilterHospital>()
    private var tempDateSelected: FilterActiveModel.FilterDate? = null

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        initRecyclerView()
        initialSortList()

        viewBinding?.run {
            if (keywordParam != null) {
                appbar.specialSearchEdtxSearch.setText(keywordParam)
            }
            if (specialistId?.isNotEmpty() == true) {
                viewModel.onFirstLaunch(specialistId, keywordParam,null,null)
            }else{
                onDeeplinkInitialAvailble(specialistIds,hospitalIds)
            }
        }
        initialFilterRecycler()


    }

    private fun onDeeplinkInitialAvailble(specialistIds: Array<String>?, hospitalIds: Array<String>?) {
        viewModel.onFirstLaunch("", "", specialistIds, hospitalIds)
    }

    private fun initialSortList() {
        viewModel.sortList.apply {
            add(
                SpecialistSortModel(
                    getString(R.string.sort_price_height_to_low),
                    "${ConstantQueryParam.QUERY_SORT}=${ConstantQueryValue.SORT_PRICE_DESC}",
                    true
                )
            )

            add(
                SpecialistSortModel(
                    getString(R.string.sort_price_low_to_height),
                    "${ConstantQueryParam.QUERY_SORT}=${ConstantQueryValue.SORT_PRICE_ASC}"
                )
            )

            add(
                SpecialistSortModel(
                    getString(R.string.sort_experience_old_to_new),
                    "${ConstantQueryParam.QUERY_SORT}=${ConstantQueryValue.SORT_WORK_DATE_DESC}"
                )
            )

            add(
                SpecialistSortModel(
                    getString(R.string.sort_experience_new_to_old),
                    "${ConstantQueryParam.QUERY_SORT}=${ConstantQueryValue.SORT_WORK_DATE_ASC}"
                )
            )
        }
    }

    private fun initRecyclerView() {
        viewBinding?.run {
            fastAdapter = FastItemAdapter()
            itemAdapter = ItemAdapter.items()
            fastAdapter.addAdapter(1, itemAdapter)

            searchSpRecyclerView.let {
                it.layoutManager = LinearLayoutManager(this@SpecialistSearchActivity)
                it.adapter = fastAdapter
            }

            filterFastAdapter = FastItemAdapter()
            searchRecyclerFilter.let {
                it.layoutManager = LinearLayoutManager(this@SpecialistSearchActivity)
                it.adapter = filterFastAdapter
            }
        }
    }

    private fun initialFilterRecycler() {
        filterFastAdapter.add(FilterActiveDayItemList { day ->
            viewModel.onFilterDayChange(day)
        }.also { it.applyChip(viewModel.filterDateEvent.value.orEmpty()) })

        filterFastAdapter.add(ContentInfoItem())

        filterFastAdapter.add(FilterActiveItemList {
            viewModel.doAfterRemoveFilter(specialSearchEdtxSearch.text.toString() ?: "",it)
            updateFilterActive(viewModel.filterActive)
        }.also {
            it.applyChip(viewModel.filterActive.filter { it !is FilterActiveModel.FilterDate })
        })

        filterFastAdapter.getItem(0)
    }

    override fun initUiListener() {
        viewBinding?.run {
            appbar.specialSearchImgFilter.onSingleClick()
                .subscribe {
                    router.openFilterBottomSheet(
                        specialistId,
                        this@SpecialistSearchActivity,
                        ::onFilterDialogCallback
                    )
                }
                .disposedBy(disposable)

            appbar.specialSearchImgSort.onSingleClick()
                .subscribe {
                    router.openSortBottomSheet(
                        viewModel.sortList,
                        this@SpecialistSearchActivity,
                        ::onSortDialogCallback
                    )
                }
                .disposedBy(disposable)


            appbar.specialSearchEdtxSearch
                .textChanges()
                .skipInitialValue()
                .debounce(800, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (it.isEmpty()) {
                        viewModel.setQueryParamDoctorSpecialist(null, specialistId)
                    } else {
                        viewModel.setQueryParamDoctorSpecialist(it.toString(), specialistId)
                    }
                }.disposedBy(disposable)

            searchSpSwipe.setOnRefreshListener {
                viewModel.fetchApiDoctorSpecialist()
            }

            fastAdapter.onClickListener = { _, _, item, _ ->
                if (item is DoctorSpecialistItem) {
                    router.openDoctorDetail(
                        this@SpecialistSearchActivity,
                        item.doctorSpecialist,
                        baseViewModel?.filterActive?.filterIsInstance<FilterActiveModel.FilterDate>()
                            ?.firstOrNull()
                    )
                }
                false
            }

            filterFastAdapter.addEventHook(object : ClickEventHook<AbstractBindingItem<*>>() {
                override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                    return if (viewHolder is FilterActiveItemList.ViewHolder) {
                        viewHolder.viewBinding.itemListBtnDelete
                    } else {
                        null
                    }
                }

                override fun onClick(
                    v: View,
                    position: Int,
                    fastAdapter: FastAdapter<AbstractBindingItem<*>>,
                    item: AbstractBindingItem<*>
                ) {
                    val filterActive: MutableList<FilterActiveModel> = viewModel
                        .filterActive
                        .filterIsInstance<FilterActiveModel.FilterDate>()
                        .toMutableList()

                    viewModel.doAfterFilter(specialSearchEdtxSearch.text.toString() ?: "",filterActive)
                    updateFilterActive(filterActive)
                }
            })
        }
    }

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun bindViewModel(): SpecialistSearchVM = viewModel

    override fun getUiBinding(): ActivitySearchSpecialistBinding {
        return ActivitySearchSpecialistBinding.inflate(layoutInflater)
    }

    private fun onSortDialogCallback(sortModel: SpecialistSortModel) {
        viewModel.reselectSortModel(sortModel)
    }

    private fun onFilterDialogCallback(triple: MutableList<FilterActiveModel>) {
        viewModel.doAfterFilter("",triple)

        updateSelectedDay(triple.filterIsInstance<FilterActiveModel.FilterDate>().firstOrNull())
        updateFilterActive(triple)
    }

    override fun observeViewModel(viewModel: SpecialistSearchVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoadingInitial)
        observe(viewModel.loadingLoadMoreEvent, ::handleLoadingLoadMore)
        observe(viewModel.specialistDoctor, ::handleInitialDoctor)
        observe(viewModel.filterDateEvent, ::handleDayFilter)
//        observe(viewModel.filterSpecialistEvent, ::handleSpecialistEvent)
//        observe(viewModel.filterHospitalEvent, ::handleHospitalEvent)
        observe(viewModel.filterSpecialistAndHospitalEvent, ::handleSpecialistAndHospital)
    }

    private fun handleSpecialistAndHospital(pair: Pair<MutableList<SpecialistFilterSpecializationModel>?, MutableList<SpecialistFilterHospitalModel>?>?) {

        pair?.first?.let { specialist ->
            tempSpecialistSelected.addAll(
                viewModel.filterActive.filterIsInstance<FilterActiveModel.FilterSpecialist>()
                    .map {
                        it.apply {
                            val specialistName = specialist
                                .filter { it.idSpecialist == idSpecialis }
                                .map { it.name }
                                .firstOrNull()
                                .orEmpty()
                            name = specialistName
                            view = specialistName
                        }
                    }
        )
        }
        pair?.second?.let { hospitals ->
            tempHospitalSelected.addAll (
                viewModel.filterActive.filterIsInstance<FilterActiveModel.FilterHospital>()
                    .map {
                        it.apply {
                            val specialistName = hospitals
                                .filter { it.idHospital == idHospital }
                                .map { it.name }
                                .firstOrNull()
                                .orEmpty()
                            name = specialistName
                            view = specialistName
                        }
                    }
            )
        }
        if (tempHospitalSelected.isNotEmpty() || tempSpecialistSelected.isNotEmpty()) {
            filterHospitalAndSpecilist.apply {
                addAll(tempSpecialistSelected)
                addAll(tempHospitalSelected)
            }
            updateFilterActive(filterHospitalAndSpecilist)
        }
    }

    private fun handleDayFilter(dayList: MutableList<SpecialistFilterDayModel>?) {
        dayList?.let {
            (filterFastAdapter.getItem(0) as FilterActiveDayItemList).applyChip(dayList)
        }
    }

    private fun updateFilterActive(filters: List<FilterActiveModel>) {
        val filterActive = filters.filter { it !is FilterActiveModel.FilterDate }
        (filterFastAdapter.getItem(2) as FilterActiveItemList).applyChip(filterActive.distinct())
        filterFastAdapter.notifyItemChanged(2)
    }

    private fun handleSpecialistEvent(listSpecialist: MutableList<SpecialistFilterSpecializationModel>?) {
        listSpecialist?.let { specialist ->
            val specialistActive =
                viewModel.filterActive.filterIsInstance<FilterActiveModel.FilterSpecialist>()
                    .map {
                        it.apply {
                            val specialistName = specialist
                                .filter { it.idSpecialist == idSpecialis }
                                .map { it.name }
                                .firstOrNull()
                                .orEmpty()
                            name = specialistName
                            view = specialistName
                        }
                    }

            updateFilterActive(specialistActive)
        }
    }

    private fun handleHospitalEvent(listHospitallist : MutableList<SpecialistFilterHospitalModel>?) {
        listHospitallist?.let { hospitals ->
         val hospitalActive = viewModel.filterActive.filterIsInstance<FilterActiveModel.FilterHospital>()
                                .map {
                                    it.apply {
                                        val specialistName = hospitals
                                            .filter { it.idHospital == idHospital }
                                            .map { it.name }
                                            .firstOrNull()
                                            .orEmpty()
                                        name = specialistName
                                        view = specialistName
                                    }
                                }

            updateFilterActive(hospitalActive)
        }
    }

    private fun handleInitialDoctor(pair: Pair<List<Doctor>, Meta>?) {
        pair?.let { data ->
            Handler(Looper.getMainLooper()).postDelayed({
                fastAdapter.clear()
                data.first.map { fastAdapter.add(DoctorSpecialistItem(it)) }

                if (data.second.totalAvailable == 0) {
                    fastAdapter.add(0, UnavailableDoctorItem())
                }
                (filterFastAdapter.getItem(1) as ContentInfoItem).updateText(data.second.totalAvailable)
                filterFastAdapter.notifyItemChanged(1)
            }, 300)

        }
    }

    private fun handleLoadingLoadMore(showLoading: Boolean?) {
        viewBinding?.searchSpRecyclerView?.post {
            if (showLoading == true && fastAdapter.itemCount > 0) {
                itemAdapter.add(ProgressBarItem())
            } else {
                itemAdapter.clear()
            }
        }
    }

    private fun updateSelectedDay(day: FilterActiveModel.FilterDate?) {
        (filterFastAdapter.getAdapterItem(0) as FilterActiveDayItemList)
            .applyChip(
                viewModel.filterDateEvent.value
                    .orEmpty()
                    .toList()
                    .map { it.apply { isSelected = day?.dayServer == dayServer } }
            )
    }

    private fun handleLoadingInitial(showLoading: Boolean?) {
        viewBinding?.searchSpSwipe?.isRefreshing = showLoading == true
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is NotFoundFailure.DataNotExist -> {}
            else -> super.handleFailure(failure)
        }
    }

    companion object {
        private const val EXTRA_SPECIALIST_ID = "SpecialistSearch.ID"
        private const val EXTRA_SPECIALIST_QUERY = "SpecialistSearch.QUERY"
        private const val EXTRA_HOSPITAL_IDS = "SpecialistSearch.IdHospitals"
        private const val EXTRA_SPECIALIST_IDS = "SpecialistSearch.IdSpecialists"
        fun createIntent(caller: Context,
                         specialistId: String? = null,
                         query: String? = null,
                         hospitalIds : Array<String>? = null,
                         specialistIds : Array<String>? = null  ) : Intent {
            return Intent(caller, SpecialistSearchActivity::class.java)
                .putExtra(EXTRA_SPECIALIST_ID, specialistId)
                .putExtra(EXTRA_SPECIALIST_QUERY, query)
                .putExtra(EXTRA_HOSPITAL_IDS,hospitalIds)
                .putExtra(EXTRA_SPECIALIST_IDS,specialistIds)
        }
    }
}