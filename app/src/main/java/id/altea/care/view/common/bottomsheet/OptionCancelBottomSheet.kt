package id.altea.care.view.common.bottomsheet

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.changeStateButton
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetCancelCallBinding
import id.altea.care.view.common.item.CancelItem
import id.altea.care.view.common.item.TextBoxItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.item_single_text_box.view.*
import java.util.concurrent.TimeUnit

class OptionCancelBottomSheet(
    val submitCallback: (String?) -> Unit,
    val dissmissCallback: (Boolean) -> Unit
) : BaseBottomSheet<BottomsheetCancelCallBinding>() {
    private val itemAdapter by lazy { GenericItemAdapter() }
    private val fastAdapter by lazy { GenericFastItemAdapter() }

    private var valueSelected: String? = ""

    override fun getUiBinding(): BottomsheetCancelCallBinding =
        BottomsheetCancelCallBinding.inflate(cloneLayoutInflater)

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {

        initRecycleView()

        viewBinding?.run {
            cancelCallBtnClose.onSingleClick().subscribe {
                dismiss()
                dissmissCallback(true)
            }.disposedBy(disposable)

            cancelCallBottomSheetBtn.onSingleClick().subscribe {
                if (valueSelected?.isNotEmpty() == true && !valueSelected.equals("Lainnya")) {
                    valueSelected?.let {
                        dismiss()
                        submitCallback(it)
                    }
                } else {
                    Toast.makeText(requireContext(), "Tidak ada yang dipilih", Toast.LENGTH_LONG).show()
                }
            }.disposedBy(disposable)
        }

    }

    private fun initRecycleView() {
        fastAdapter.addAdapter(1, itemAdapter)
        fastAdapter.add(
            CancelItem("Tidak sengaja membuat order"),
            CancelItem("Ingin ubah jadwal"),
            CancelItem("Ingin ubah Dokter Spesialis"),
            CancelItem("Lainnya")
        )

        viewBinding?.cancelCallBottomSheetRecyclerview?.run {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = fastAdapter
        }

        fastAdapter.addEventHook(CancelItem.CancelClickEvent {
            Single.just(it).delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { t1, _ ->
                    Handler(Looper.getMainLooper()).postDelayed({
                        valueSelected = t1
                    }, 200)
                    viewBinding?.cancelCallBottomSheetBtn?.changeStateButton(true)
                    if (t1.equals("Lainnya")) {
                        viewBinding?.cancelCallBottomSheetBtn?.changeStateButton(false)
                        itemAdapter.clear()
                        itemAdapter.add(TextBoxItem())
                        fastAdapter.itemCount
                        val item = fastAdapter.getItem(4) as TextBoxItem
                     item.setListenerText(object : TextBoxItem.onTextChangeListener{
                         override fun textChangeListener(value: String) {
                             valueSelected = value
                             if (value != "") {
                                 viewBinding?.cancelCallBottomSheetBtn?.changeStateButton(true)
                             }
                         }
                     })

                    } else {
                        viewBinding?.cancelCallBottomSheetRecyclerview?.findViewHolderForAdapterPosition(4)?.itemView?.textContentMessageCallEnded?.setText("")
                        viewBinding?.cancelCallBottomSheetRecyclerview?.findViewHolderForAdapterPosition(4)?.itemView?.textContentMessageCallEnded?.clearFocus()
                        itemAdapter.clear()
                        fastAdapter.notifyAdapterDataSetChanged()
                    }
                }.disposedBy(disposable)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dissmissCallback(true)
        dismiss()
    }

    companion object {
        fun newInstance(
            submitCallback: (String?) -> Unit,
            dissmissCallback: (Boolean) -> Unit
        ): BottomSheetDialogFragment {
            return OptionCancelBottomSheet(submitCallback, dissmissCallback)
        }
    }

}