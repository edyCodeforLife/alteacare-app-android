package id.altea.care.view.family.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxbinding3.widget.textChanges
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ItemFilterListener
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.utils.ComparableItemListImpl
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.domain.model.SelectedModel
import id.altea.care.core.ext.disposedBy
import id.altea.care.databinding.BottomsheetGeneralFilterSingleSelectBinding
import id.altea.care.view.address.addressform.bottomsheet.AddressFormBottomSheet
import id.altea.care.view.address.addressform.bottomsheet.BottomSheetType
import id.altea.care.view.address.addressform.bottomsheet.SelectedComparatorAsc
import id.altea.care.view.address.addressform.item.ItemAddressSingleSelectEvent
import id.altea.care.view.address.addressform.item.ItemAddressSingleSelectable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class FamilyFormBottomSheet (
    private val submitCallback: (SelectedModel) -> Unit,
    private val dismissCallback: () -> Unit
) : BaseBottomSheet<BottomsheetGeneralFilterSingleSelectBinding>() {
    private var data: SelectedModel? = null
    private var listData: ArrayList<SelectedModel> = arrayListOf()
    private var bottomSheetType: BottomSheetType? = null

    private lateinit var itemAdapter: ItemAdapter<ItemAddressSingleSelectable>
    private lateinit var fastAdapter: FastAdapter<ItemAddressSingleSelectable>
    private lateinit var extension: SelectExtension<ItemAddressSingleSelectable>
    private lateinit var comparatorAsc: ComparableItemListImpl<ItemAddressSingleSelectable>

    override fun getUiBinding(): BottomsheetGeneralFilterSingleSelectBinding {
        return BottomsheetGeneralFilterSingleSelectBinding.inflate(layoutInflater)
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
        comparatorAsc = ComparableItemListImpl(null)
        itemAdapter = ItemAdapter(comparatorAsc)
        fastAdapter = FastAdapter.Companion.with(itemAdapter)

        extension = fastAdapter.getSelectExtension()
        extension.isSelectable = true

        data = arguments?.getParcelable(EXTRA_DATA)
        listData = arguments?.getParcelableArrayList(EXTRA_LIST_DATA) ?: arrayListOf()

        viewBinding?.run {
            filterGeneralSingleRecyclerView.let {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = fastAdapter
            }

            // region listener
            filterGeneralSingleImgClose.setOnClickListener { dismiss() }

            filterGeneralSingleEdtxSearch.textChanges()
                .skipInitialValue()
                .debounce(800, TimeUnit.MILLISECONDS)
                .doOnNext { Timber.e(it.toString()) }
                .subscribe { itemAdapter.filter(it) }
                .disposedBy(disposable)


            fastAdapter.addEventHook(ItemAddressSingleSelectEvent { selectedModel ->
                Observable.just(selectedModel)
                    .delay(800, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        submitCallback(it)
                        dismiss()
                    }
                    .disposedBy(disposable)
            })

            itemAdapter.itemFilter.filterPredicate = { item, constraint ->
                item.data.getTitle().contains(constraint.toString(), true)
            }

            itemAdapter.itemFilter.itemFilterListener =
                object : ItemFilterListener<ItemAddressSingleSelectable> {
                    override fun itemsFiltered(
                        constraint: CharSequence?,
                        results: List<ItemAddressSingleSelectable>?
                    ) {
                        itemAdapter.clear()
                        results?.let { itemAdapter.add(it) }
                    }

                    override fun onReset() {
                        filterGeneralSingleRecyclerView.post { setDefaultList() }
                    }
                }
            //endregion
        }

        setDefaultList()
    }

    private fun setDefaultList() {
        listData.map { selectedModel ->
            if (selectedModel.getIdModel() == data?.getIdModel()) {
                itemAdapter.add(ItemAddressSingleSelectable(selectedModel).apply { setChecked(true) })
            } else {
                itemAdapter.add(ItemAddressSingleSelectable(selectedModel).apply { setChecked(false) })
            }
        }
        comparatorAsc.withComparator(SelectedComparatorAsc())
    }

    companion object {
        private const val EXTRA_LIST_DATA = "FamilyFormBottomSheet.listData"
        private const val EXTRA_DATA = "FamilyFormBottomSheet.data"
        fun newInstance(
            data: SelectedModel?,
            listData: ArrayList<SelectedModel>,
            submitCallback: (SelectedModel) -> Unit,
            dismissCallback: (() -> Unit) = {}
        ): FamilyFormBottomSheet {
            return FamilyFormBottomSheet(submitCallback, dismissCallback).apply {
                val bundle = Bundle().apply {
                    putParcelable(EXTRA_DATA, data)
                    putParcelableArrayList(EXTRA_LIST_DATA, listData)
                }
                arguments = bundle
            }
        }
    }
}

class SelectedComparatorAsc : Comparator<ItemAddressSingleSelectable> {
    override fun compare(o1: ItemAddressSingleSelectable?, o2: ItemAddressSingleSelectable?): Int {
        return when {
            (o1 == null || o2 == null) -> 0
            else -> o2.data.isChecked.compareTo(o1.data.isChecked)
        }
    }

}