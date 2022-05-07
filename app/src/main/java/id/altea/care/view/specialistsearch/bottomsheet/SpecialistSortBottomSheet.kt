package id.altea.care.view.specialistsearch.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetSortSpecialistBinding
import id.altea.care.view.specialistsearch.item.SpecialistSortItem
import id.altea.care.view.specialistsearch.model.SpecialistSortModel

class SpecialistSortBottomSheet(
    private val callback: (SpecialistSortModel) -> Unit,
    private val dismissCallback: () -> Unit
) : BaseBottomSheet<BottomsheetSortSpecialistBinding>() {

    private val itemAdapter = ItemAdapter<SpecialistSortItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private lateinit var selectExtension: SelectExtension<SpecialistSortItem>


    override fun getUiBinding(): BottomsheetSortSpecialistBinding {
        return BottomsheetSortSpecialistBinding.inflate(cloneLayoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
        initUiListener()
    }

    private fun initRecyclerView() {
        selectExtension = fastAdapter.getSelectExtension()
        selectExtension.isSelectable = true
        requireArguments().getParcelableArrayList<SpecialistSortModel>(EXTRA_SORT_CONTENT)?.let {
            it.map { sortModel ->
                val item = SpecialistSortItem(sortModel)
                itemAdapter.add(item)
                if (sortModel.isSelected) {
                    fastAdapter.getSelectExtension().select(item, false)
                }
            }
        }

        viewBinding?.sortBottomSheetRecycler?.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fastAdapter
        }
    }

    private fun initUiListener() {
        viewBinding?.run {
            fastAdapter.addEventHook(SpecialistSortItem.SortClickEvent {

            })

            sortBottomSheetBtnSelect.onSingleClick()
                .subscribe {
                    callback(itemAdapter.itemFilter.selectedItems.first().sortModel)
                    dismiss()
                }
                .disposedBy(disposable)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissCallback()
    }

    companion object {
        private const val EXTRA_SORT_CONTENT = "SpecialistSortBottom.content"
        fun newInstance(
            sortContent: ArrayList<SpecialistSortModel>,
            submitCallback: ((SpecialistSortModel) -> Unit) = { },
            dismissCallback: (() -> Unit) = { }
        ): BottomSheetDialogFragment {
            return SpecialistSortBottomSheet(submitCallback, dismissCallback).apply {
                val bundle = Bundle()
                bundle.putParcelableArrayList(EXTRA_SORT_CONTENT, sortContent)
                arguments = bundle
            }
        }
    }
}
