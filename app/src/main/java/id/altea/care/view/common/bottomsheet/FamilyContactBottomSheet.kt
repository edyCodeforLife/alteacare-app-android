package id.altea.care.view.common.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.domain.model.FamilyContact
import id.altea.care.core.ext.disposedBy
import id.altea.care.databinding.BottomsheetFamilyContactBinding
import id.altea.care.view.common.item.FamilyContactItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class FamilyContactBottomSheet( private val familyContact: List<FamilyContact>?,
                                private val callback: (FamilyContact) -> Unit,
                                private val dismissCallback: () -> Unit) : BaseBottomSheet<BottomsheetFamilyContactBinding>() {

    private val itemAdapter = ItemAdapter<FamilyContactItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun getUiBinding(): BottomsheetFamilyContactBinding  = BottomsheetFamilyContactBinding.inflate(cloneLayoutInflater)

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()

    }

    private fun initRecyclerView() {
        familyContact?.let {
            it.map {familyContact ->
                itemAdapter.add(FamilyContactItem(familyContact))
            }
        }
        viewBinding?.familyContactBottomSheetRecyclerview?.run {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = fastAdapter
        }

        fastAdapter.addEventHook(FamilyContactItem.FamilyClickEvent {
            Single.just(it).delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { t1, _ ->
                    dismiss()
                    callback(t1)
                }.disposedBy(disposable)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissCallback()
    }

    companion object {
        fun newInstance(
            familyContact: List<FamilyContact>?,
            submitCallback: (FamilyContact) -> Unit,
            dismissCallback: () -> Unit
        ): BottomSheetDialogFragment {
            return FamilyContactBottomSheet(familyContact,submitCallback, dismissCallback)
        }
    }

}