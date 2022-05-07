package id.altea.care.view.specialistsearch.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxbinding3.widget.textChanges
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import id.altea.care.core.base.BaseBottomSheetVM
import id.altea.care.core.ext.disposedBy
import id.altea.care.databinding.BottomsheetFilterAllSpecialistBinding
import id.altea.care.view.specialistsearch.SpecialistSearchVM
import id.altea.care.view.specialistsearch.item.FilterHospitalItem
import id.altea.care.view.specialistsearch.model.FilterActiveModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterHospitalModel
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class FilterAllHospitalBottomSheet(
    val submitCallback: (ArrayList<FilterActiveModel.FilterHospital>) -> Unit
) : BaseBottomSheetVM<BottomsheetFilterAllSpecialistBinding, SpecialistSearchVM>() {

    private lateinit var fastAdapter: GenericFastItemAdapter

    private lateinit var hospital: List<SpecialistFilterHospitalModel>
    private lateinit var tempSelected: MutableList<FilterActiveModel.FilterHospital>

    override fun getUiBinding(): BottomsheetFilterAllSpecialistBinding {
        return BottomsheetFilterAllSpecialistBinding.inflate(cloneLayoutInflater)
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
        fastAdapter = FastItemAdapter()
        val selectExtension = fastAdapter.getSelectExtension()
        selectExtension.isSelectable = true

        viewBinding?.filterAllSpecialistRecyclerView?.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fastAdapter
        }
        hospital = viewModel?.filterHospitalEvent?.value.orEmpty()
        tempSelected = arguments?.getParcelableArrayList(EXTRA_HOSPITAL) ?: mutableListOf()

        fastAdapter.add(hospital.map { hospitalModel ->

            FilterHospitalItem(hospitalModel.apply {
                isSelected = tempSelected.map { it.idHospital }.contains(idHospital)
            }) { clickCallback ->

                val indexTemp = tempSelected
                    .withIndex()
                    .find { it.value.idHospital == clickCallback.first }

                if (indexTemp != null && !clickCallback.third) {
                    tempSelected.removeAt(indexTemp.index)
                } else if (indexTemp == null && clickCallback.third) {
                    tempSelected.add(
                        FilterActiveModel.FilterHospital(
                            clickCallback.first,
                            clickCallback.second
                        )
                    )
                }
            }
        })
        initUiListener()
    }

    private fun initUiListener() {
        viewBinding?.run {
            filterAllSpecialistEdtxSearch.textChanges()
                .skipInitialValue()
                .debounce(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    fastAdapter.filter(it)
                    fastAdapter
                        .itemFilter
                        .filterPredicate = { item, constraint ->
                        (item as FilterHospitalItem).data.name.contains(
                            constraint.toString(),
                            ignoreCase = true
                        )
                    }
                }
                .disposedBy(disposable)

            filterAllSpecialistImgClose.setOnClickListener { dismiss() }
            filterAllSpecialistBtnSave.setOnClickListener {
                submitCallback(ArrayList(tempSelected))
                dismiss()
            }
        }
    }

    companion object {
        private const val EXTRA_HOSPITAL = "FilterAllHospital.selected"
        fun newInstance(
            selectedHospital: ArrayList<FilterActiveModel.FilterHospital> = arrayListOf(),
            submitCallback: ((ArrayList<FilterActiveModel.FilterHospital>) -> Unit) = { }
        ): FilterAllHospitalBottomSheet {
            return FilterAllHospitalBottomSheet(submitCallback).apply {
                val bundle = Bundle()
                bundle.putParcelableArrayList(EXTRA_HOSPITAL, selectedHospital)
                arguments = bundle
            }
        }
    }

    override fun observeViewModel(viewModel: SpecialistSearchVM) {
    }

    override fun bindViewModel(): SpecialistSearchVM {
        return ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[SpecialistSearchVM::class.java]
    }
}

