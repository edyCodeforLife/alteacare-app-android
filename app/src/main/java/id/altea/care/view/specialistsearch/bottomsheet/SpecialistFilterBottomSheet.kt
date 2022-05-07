package id.altea.care.view.specialistsearch.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import id.altea.care.R
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.ChoosingDayPayloadBuilder
import id.altea.care.core.base.BaseBottomSheetVM
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetFilterSpecialistBinding
import id.altea.care.databinding.ItemFilterTitleBinding
import id.altea.care.view.specialistsearch.SpecialistSearchVM
import id.altea.care.view.specialistsearch.item.FilterContentItemList
import id.altea.care.view.specialistsearch.item.FilterPriceItemList
import id.altea.care.view.specialistsearch.item.FilterTitleItem
import id.altea.care.view.specialistsearch.model.*
import id.altea.care.view.specialistsearch.model.TitleType.*
import javax.inject.Inject

class SpecialistFilterBottomSheet(
    private val callback: ((MutableList<FilterActiveModel>) -> Unit) = { },
    private val dismissCallback: () -> Unit
) : BaseBottomSheetVM<BottomsheetFilterSpecialistBinding, SpecialistSearchVM>() {

    @Inject
    lateinit var analyticManager: AnalyticManager

    private val itemAdapter by lazy { ItemAdapter<IItem<*>>() }
    private val fastAdapter by lazy { FastAdapter.with(itemAdapter) }
    private lateinit var selectExtension: SelectExtension<IItem<*>>

    private var tempSpecialistSelected = mutableListOf<FilterActiveModel.FilterSpecialist>()
    private var tempHospitalSelected = mutableListOf<FilterActiveModel.FilterHospital>()
    private var tempPriceSelected: FilterActiveModel.FilterPrice? = null
    private var tempDateSelected: FilterActiveModel.FilterDate? = null
    private var tempSelected = mutableListOf<FilterActiveModel>()

    private var defaultSpecialistId: String? = null

    override fun getUiBinding(): BottomsheetFilterSpecialistBinding {
        return BottomsheetFilterSpecialistBinding.inflate(cloneLayoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }

        return dialog
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        defaultSpecialistId = arguments?.getString(EXTRA_DEFAULT_SPECIALIST)
        tempSpecialistSelected = viewModel?.filterActive
            ?.filterIsInstance<FilterActiveModel.FilterSpecialist>()
            .orEmpty()
            .toMutableList()

        tempHospitalSelected = viewModel?.filterActive
            ?.filterIsInstance<FilterActiveModel.FilterHospital>()
            .orEmpty()
            .toMutableList()

        tempPriceSelected = viewModel?.filterActive
            ?.filterIsInstance<FilterActiveModel.FilterPrice>()
            ?.firstOrNull()

        tempDateSelected = viewModel?.filterActive
            ?.filterIsInstance<FilterActiveModel.FilterDate>()
            ?.firstOrNull()

        initRecyclerView()
        initUiListener()
        initialDefaultValue()
        sendTrackingEventChoosingDayToAnalytics(tempDateSelected?.dayServer ?: "")
    }

    private fun initRecyclerView() {
        selectExtension = fastAdapter.getSelectExtension()
        selectExtension.isSelectable = true

        viewBinding?.filterBottomSheetRecycler?.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fastAdapter
        }
    }

    private fun initialDefaultValue() {
        itemAdapter.add(FilterTitleItem(FilterTitle(getString(R.string.day), DAY, false)))
        itemAdapter.add(FilterPriceItemList { filterModel ->
            val filterDay = filterModel as SpecialistFilterDayModel
            tempDateSelected = if (filterDay.isSelected) {
                FilterActiveModel.FilterDate(
                    filterDay.dayLocale, filterDay.dayServer, filterDay.date
                )
            } else {
                null
            }
        }.also {
            it.applyChip(viewModel?.filterDateEvent?.value.orEmpty()
                .map {
                    it.apply {
                        isSelected = tempDateSelected != null &&
                                it.dayServer == tempDateSelected?.dayServer
                    }
                })
        })

        itemAdapter.add(
            FilterTitleItem(FilterTitle(getString(R.string.nav_specialist), SPECIALIST, true))
        )

        /**
         * initial list specialization
         */
        itemAdapter.add(
            FilterContentItemList { callback ->
                val selectedModel = (callback as SpecialistFilterSpecializationModel).idSpecialist
                val indexTemp = tempSpecialistSelected
                    .withIndex()
                    .find { it.value.idSpecialis == selectedModel }

                if (indexTemp != null) {
                    tempSpecialistSelected.removeAt(indexTemp.index)
                } else {
                    tempSpecialistSelected.add(
                        FilterActiveModel.FilterSpecialist(
                            callback.idSpecialist,
                            callback.name
                        )
                    )
                }
            }.apply {
                applyChip(
                    updateSelectedSpecialist(
                        viewModel?.filterSpecialistEvent?.value.orEmpty(),
                        viewModel?.filterActive
                            ?.filterIsInstance<FilterActiveModel.FilterSpecialist>()
                            .orEmpty()
                            .toMutableList()
                    )
                )
            }
        )

        itemAdapter.add(
            FilterTitleItem(
                FilterTitle(
                    getString(R.string.hospital),
                    HOSPITAL,
                    viewModel?.filterHospitalEvent?.value?.size ?: 0 > 5
                )
            )
        )

        /**
         * initial list hospital
         */
        itemAdapter.add(
            FilterContentItemList { callback ->
                val selectedModel = (callback as SpecialistFilterHospitalModel).idHospital
                val indexTemp = tempHospitalSelected
                    .withIndex()
                    .find { it.value.idHospital == selectedModel }

                if (indexTemp != null) {
                    tempHospitalSelected.removeAt(indexTemp.index)
                } else {
                    tempHospitalSelected.add(
                        FilterActiveModel.FilterHospital(
                            callback.idHospital,
                            callback.name
                        )
                    )
                }
            }.apply {
                applyChip(updateSelectedHospital(tempHospitalSelected))
            }
        )

        itemAdapter.add(
            FilterTitleItem(FilterTitle(getString(R.string.price), PRICE, false))
        )

        itemAdapter.add(
            FilterPriceItemList {
                val filterPrice = (it as SpecialistFilterPriceModel)
                tempPriceSelected = if (filterPrice.isSelected) {
                    FilterActiveModel.FilterPrice(filterPrice.filterPriceType, filterPrice.content)
                } else {
                    null
                }
            }.apply {
                applyChip(viewModel?.filterPriceEvent?.value
                    ?.map {
                        it.apply {
                            isSelected = (tempPriceSelected != null && it.filterPriceType.equals(
                                tempPriceSelected?.priceType
                            ))
                        }
                    }
                    .orEmpty()
                    .toList())
            }
        )
    }

    private fun sendTrackingEventChoosingDayToAnalytics(day: String) {
        analyticManager.trackingEventChoosingDay(ChoosingDayPayloadBuilder(day))
    }

    private fun initUiListener() {
        viewBinding?.run {
            filterBottomSheetBtnSelect.onSingleClick()
                .subscribe {
                    callback(
                        tempSelected.apply {
                            clear()
                            addAll(tempSpecialistSelected)
                            addAll(tempHospitalSelected)
                            tempPriceSelected?.let { add(it) }
                            tempDateSelected?.let { add(it) }
                        }
                    )
                    dismiss()
                }
                .disposedBy(disposable)

            filterBottomTxtReset.onSingleClick()
                .subscribe {
                    itemAdapter.adapterItems.forEachIndexed { index, iItem ->
                        when (index) {
                            1 -> {
                                tempDateSelected = null
                                (iItem as FilterPriceItemList).applyChip(
                                    viewModel?.filterDateEvent?.value.orEmpty().map {
                                        it.apply { isSelected = false }
                                    }
                                )
                            }
                            3 -> {
                                tempSpecialistSelected = mutableListOf()
                                (iItem as FilterContentItemList).applyChip(
                                    updateSelectedSpecialist(
                                        viewModel?.filterSpecialistEvent?.value.orEmpty(),
                                        tempSpecialistSelected
                                    )
                                )
                            }
                            5 -> {
                                tempHospitalSelected = mutableListOf()
                                (iItem as FilterContentItemList).applyChip(
                                    updateSelectedHospital(tempHospitalSelected)
                                )
                            }
                            7 -> {
                                tempPriceSelected = null
                                (iItem as FilterPriceItemList).applyChip(
                                    viewModel?.filterPriceEvent?.value.orEmpty().map {
                                        it.apply { isSelected = false }
                                    }
                                )
                            }
                        }
                    }
                }
                .disposedBy(disposable)
        }

        fastAdapter.addEventHook(object : ClickEventHook<IItem<*>>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemFilterTitleBinding) {
                    return (viewHolder.binding as ItemFilterTitleBinding).itemFilterTxtShowAll
                }
                return null
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<IItem<*>>,
                item: IItem<*>
            ) {
                if (item is FilterTitleItem) {
                    when (item.filterTitle.titleType) {
                        HOSPITAL -> FilterAllHospitalBottomSheet
                            .newInstance(ArrayList(tempHospitalSelected)) {
                                tempHospitalSelected.clear()
                                tempHospitalSelected.addAll(it)

                                (fastAdapter.getItem(5) as FilterContentItemList)
                                    .applyChip(updateSelectedHospital(it))
                            }.show(childFragmentManager, "filter_hospital")

                        SPECIALIST -> {
                            FilterAllSpecialistBottomSheet
                                .newInstance(ArrayList(tempSpecialistSelected)) {
                                    val subSpecialist = viewModel?.filterSpecialistEvent
                                        ?.value
                                        ?.flatMap { it.subSpecialist }
                                        .orEmpty()

                                    val specialistList = viewModel?.filterSpecialistEvent
                                        ?.value?.toList().orEmpty() + subSpecialist

                                    tempSpecialistSelected.clear()
                                    tempSpecialistSelected.addAll(it)


                                    (fastAdapter.getItem(3) as FilterContentItemList)
                                        .applyChip(updateSelectedSpecialist(specialistList, it))
                                }.show(childFragmentManager, "filterAll")
                        }
                        else -> {
                        }
                    }
                }
            }
        })
    }

    private fun updateSelectedHospital(
        selectedHospital: MutableList<FilterActiveModel.FilterHospital>
    ): List<SpecialistFilterHospitalModel> {
        val idsSelected = selectedHospital.map { it.idHospital }
        return viewModel?.filterHospitalEvent?.value
            ?.map { model ->
                model.apply { isSelected = idsSelected.contains(model.idHospital) }
            }
            ?.sortedBy { !it.isSelected }
            ?.take(5)
            .orEmpty()
            .toList()
    }

    private fun updateSelectedSpecialist(
        source: List<SpecialistFilterSpecializationModel>,
        selectedSpecialist: MutableList<FilterActiveModel.FilterSpecialist>
    ): List<SpecialistFilterSpecializationModel> {
        val idsSelected = selectedSpecialist.map { it.idSpecialis }
        return source
            .asSequence()
            .map { model ->
                model.apply {
                    isSelected = idsSelected.contains(idSpecialist)
                    subSpecialist.map { child ->
                        child.apply {
                            isSelected = idsSelected.contains(idSpecialist)
                        }
                    }
                }
            }
            .filter { it.isPopular || it.idSpecialist == defaultSpecialistId || it.isSelected }
            .sortedBy { !it.isSelected }
            .take(5)
            .toList()
    }

    override fun observeViewModel(viewModel: SpecialistSearchVM) {
    }

    override fun bindViewModel(): SpecialistSearchVM {
        return ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[SpecialistSearchVM::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
        dismissCallback()
        viewBinding = null
    }

    companion object {
        private const val EXTRA_DEFAULT_SPECIALIST = "Filter.defaultSpecialist"

        fun newInstance(
            defaultSpecialistId: String?,
            submitCallback: ((MutableList<FilterActiveModel>) -> Unit) = { },
            dismissCallback: (() -> Unit) = { }
        ): BottomSheetDialogFragment {
            return SpecialistFilterBottomSheet(submitCallback, dismissCallback)
                .apply {
                    val bundle = Bundle()
                    bundle.putString(EXTRA_DEFAULT_SPECIALIST, defaultSpecialistId)
                    arguments = bundle
                }
        }
    }

}