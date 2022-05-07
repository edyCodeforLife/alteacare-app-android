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
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import id.altea.care.core.base.BaseBottomSheetVM
import id.altea.care.core.ext.disposedBy
import id.altea.care.databinding.BottomsheetFilterAllSpecialistBinding
import id.altea.care.view.specialistsearch.SpecialistSearchVM
import id.altea.care.view.specialistsearch.item.FilterSpecialistChildItem
import id.altea.care.view.specialistsearch.item.FilterSpecialistParentItem
import id.altea.care.view.specialistsearch.model.FilterActiveModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterSpecializationModel
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class FilterAllSpecialistBottomSheet(
    val submitCallback: (ArrayList<FilterActiveModel.FilterSpecialist>) -> Unit
) : BaseBottomSheetVM<BottomsheetFilterAllSpecialistBinding, SpecialistSearchVM>() {

    private lateinit var fastItemAdapter: GenericFastItemAdapter
    private lateinit var specialists: List<SpecialistFilterSpecializationModel>
    private lateinit var tempSelected: MutableList<FilterActiveModel.FilterSpecialist>

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
        fastItemAdapter = FastItemAdapter()
        fastItemAdapter.getExpandableExtension()

        viewBinding?.filterAllSpecialistRecyclerView?.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fastItemAdapter
        }
        specialists = viewModel?.filterSpecialistEvent?.value.orEmpty()
        tempSelected = arguments?.getParcelableArrayList(EXTRA_SPECIALISTS) ?: mutableListOf()

        checkedSelected(tempSelected.map { it.idSpecialis })

        val items = ArrayList<GenericItem>()
        // initial parent specialist
        specialists.filter { !it.isChild }.forEach {
            val parent = FilterSpecialistParentItem(it) { triple ->
                // on checkbox listener
                updateTempSelected(
                    SpecialistFilterSpecializationModel(
                        triple.first, triple.second, triple.third
                    )
                )

                val specialistSelect = specialists.find { it.idSpecialist == triple.first }
                if (specialistSelect?.subSpecialist?.isNotEmpty() == true) {
                    specialistSelect.subSpecialist.mapIndexed { _, sub ->
                        updateTempSelected(
                            SpecialistFilterSpecializationModel(
                                sub.idSpecialist, sub.name, triple.third
                            )
                        )
                        return@mapIndexed sub.apply { isSelected = triple.third }
                    }

                    viewBinding?.filterAllSpecialistRecyclerView?.postDelayed({
                        fastItemAdapter.notifyDataSetChanged()
                    }, 100)
                }
            }

            it.subSpecialist.map { specializationModel ->
                // initial sub specialist and checked listener
                parent.subItems.add(FilterSpecialistChildItem(specializationModel) { childClick ->

                    updateTempSelected(
                        SpecialistFilterSpecializationModel(
                            childClick.childSpecialistId,
                            childClick.childSpecialistName,
                            childClick.isChecked
                        )
                    )

                    val specialistSelect =
                        specialists.find { it.idSpecialist == childClick.parentSpecialistId }

                    specialistSelect?.apply {

                        specialistSelect.isSelected =
                            subSpecialist.filter { it.isSelected }.size == subSpecialist.size

                        updateTempSelected(this)
                    }

                    viewBinding?.filterAllSpecialistRecyclerView?.postDelayed({
                        fastItemAdapter.notifyDataSetChanged()
                    }, 100)
                })
            }
            items.add(parent)
        }
        fastItemAdapter.add(items)
        initUiListener()
    }

    private fun initUiListener() {
        viewBinding?.run {
            filterAllSpecialistEdtxSearch.textChanges()
                .skipInitialValue()
                .debounce(800, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    fastItemAdapter.getExpandableExtension().collapse()
                    fastItemAdapter.filter(it)
                    fastItemAdapter
                        .itemFilter
                        .filterPredicate = { item, constraint ->
                        (item as FilterSpecialistParentItem).data.name.contains(
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
        private const val EXTRA_SPECIALISTS = "FilterAllSpecialist.specialists"
        fun newInstance(
            selectedSpecialist: ArrayList<FilterActiveModel.FilterSpecialist> = arrayListOf(),
            submitCallback: ((ArrayList<FilterActiveModel.FilterSpecialist>) -> Unit) = { }
        ): FilterAllSpecialistBottomSheet {
            return FilterAllSpecialistBottomSheet(submitCallback).apply {
                val bundle = Bundle()
                bundle.putParcelableArrayList(EXTRA_SPECIALISTS, selectedSpecialist)
                arguments = bundle
            }
        }
    }

    private fun checkedSelected(ids: List<String>) {
        specialists.map { parent ->
            parent.apply {
                isSelected = ids.contains(parent.idSpecialist)
                subSpecialist.map { child ->
                    child.apply {
                        isSelected = ids.contains(child.idSpecialist)
                    }
                }
            }
        }
    }

    private fun updateTempSelected(specializationModel: SpecialistFilterSpecializationModel) {
        val indexTemp = tempSelected
            .withIndex()
            .find { it.value.idSpecialis == specializationModel.idSpecialist }

        if (indexTemp != null && !specializationModel.isSelected) {
            tempSelected.removeAt(indexTemp.index)
        } else if (indexTemp == null && specializationModel.isSelected) {
            tempSelected.add(
                FilterActiveModel.FilterSpecialist(
                    specializationModel.idSpecialist,
                    specializationModel.name
                )
            )
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

