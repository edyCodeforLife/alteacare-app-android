package id.altea.care.view.common.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.R
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.disposedBy
import id.altea.care.databinding.BottomsheetGenderBinding
import id.altea.care.view.common.item.GenderItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Created by trileksono on 04/03/21.
 */
class GenderBottomSheet(
    private val callback: (String) -> Unit,
    private val dismissCallback: () -> Unit
) : BaseBottomSheet<BottomsheetGenderBinding>() {

    private val itemAdapter = ItemAdapter<GenderItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun getUiBinding(): BottomsheetGenderBinding {
        return BottomsheetGenderBinding.inflate(cloneLayoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        itemAdapter.add(
            GenderItem(getString(R.string.gender_male)),
            GenderItem(getString(R.string.gender_female))
        )
        viewBinding?.genderBottomSheetRecyclerview?.run {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = fastAdapter
        }

        fastAdapter.addEventHook(GenderItem.GenderClickEvent {
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
            submitCallback: (String) -> Unit,
            dismissCallback: () -> Unit
        ): BottomSheetDialogFragment {
            return GenderBottomSheet(submitCallback, dismissCallback)
        }
    }
}
