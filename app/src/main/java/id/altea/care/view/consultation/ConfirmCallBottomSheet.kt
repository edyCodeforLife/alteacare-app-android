package id.altea.care.view.consultation

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.R
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.domain.model.InformationVideoCall
import id.altea.care.core.ext.autoSize
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetConfirmantionGpBinding
import id.altea.care.view.common.item.ItemInformationVideoCall
import id.altea.care.view.common.item.ItemOrderMedicine
import kotlinx.android.synthetic.main.bottomsheet_confirmantion_gp.*

class ConfirmCallBottomSheet(
    private val callback: (String) -> Unit,
    private val dismissCallback: () -> Unit
    ) : BaseBottomSheet<BottomsheetConfirmantionGpBinding>() {

    private val itemAdapter = ItemAdapter<ItemInformationVideoCall>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

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

    override fun getUiBinding(): BottomsheetConfirmantionGpBinding  = BottomsheetConfirmantionGpBinding.inflate(cloneLayoutInflater)

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        dialogBtnDoctor.onSingleClick().subscribe {
            callback("clicked")
            dismiss()
        }.disposedBy(disposable)

        dialogBtnClose.onSingleClick().subscribe {
            dismiss()
        }.disposedBy(disposable)

        itemAdapter.add(
            ItemInformationVideoCall(InformationVideoCall(R.drawable.ic_pesonal_id,"Pada telekonsultasi pertama, <br><b>Anda wajib menyiapkan KTP</b> <br>untuk validasi identitas")),
            ItemInformationVideoCall(InformationVideoCall(R.drawable.ic_document,"Siapkan <b>dokumen penunjang <br>medis digital</b> yang diperlukan <br>(hasil lab, radiologi, dsb)")),
            ItemInformationVideoCall(InformationVideoCall(R.drawable.ic_phone_ringing,"Pastikan <b>koneksi internet stabil, <br>baterai cukup dan masuk ke <br>ruangan yang nyaman</b> agar <br>telekonsultasi berjalan lancar"))
        )
        itemAdapter.adapterItems.firstOrNull()?.setDisposable(disposable)
        confirmGpRecyler.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = fastAdapter
        }
        textView28.autoSize(disposable)
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
            return ConfirmCallBottomSheet(
                submitCallback,
                dismissCallback
            )
        }
    }
}