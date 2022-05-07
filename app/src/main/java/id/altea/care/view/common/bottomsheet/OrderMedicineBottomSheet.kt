package id.altea.care.view.common.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.util.Log
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
import id.altea.care.databinding.BottomsheetOrderMedicineBinding
import id.altea.care.view.common.item.GenderItem
import id.altea.care.view.common.item.ItemOrderMedicine
import id.altea.care.core.domain.model.OrderMedicine
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import kotlinx.android.synthetic.main.bottomsheet_order_medicine.*

class OrderMedicineBottomSheet() : BaseBottomSheet<BottomsheetOrderMedicineBinding>() {

    private val itemAdapter = ItemAdapter<ItemOrderMedicine>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun getUiBinding(): BottomsheetOrderMedicineBinding = BottomsheetOrderMedicineBinding.inflate(cloneLayoutInflater)

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
       viewBinding?.run {
           orderMedicineButton.onSingleClick().subscribe {
              dismiss()
           }.disposedBy(disposable)
       }
        initRecycleView()

    }

    private fun initRecycleView() {
        itemAdapter.add(
            ItemOrderMedicine(OrderMedicine(R.drawable.ic_info_obat_01,"1.Anda akan dihubunggi oleh","petugas farmasi Rumah Sakit 30 menit setelah catatan medis diterima")),
            ItemOrderMedicine(OrderMedicine(R.drawable.ic_info_obat_02,"2.Konfirmasi pembelian obat","dan alamat pengiriman obat kepada petugas farmasi")),
            ItemOrderMedicine(OrderMedicine(R.drawable.ic_info_obat_03,"3.Selesaikan pembayaran", "online yang tersedia untuk membeli obat")),
            ItemOrderMedicine(OrderMedicine(R.drawable.ic_info_obat_04,"4.Obat anda akan dikirim ke","alamat tujuan")),
            ItemOrderMedicine(OrderMedicine(R.drawable.ic_info_obat_05,"5. Yeay! Obat sudah sampai","di tujuan"))
        )

        viewBinding?.orderMedicineRecyclr?.run{
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = fastAdapter
        }
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

    companion object {
        fun newInstance(
        ): BottomSheetDialogFragment {
            return OrderMedicineBottomSheet()
        }
    }
}