package id.altea.care.view.myconsultation.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.databinding.BottomsheetFilterMyconsultationBinding
import id.altea.care.view.myconsultation.item.ConsultationFilterItem
import id.altea.care.view.myconsultation.item.FilterEventHook

class ConsultationFilterBottomSheet(
    private val callbackPatient: (PatientFamily?) -> Unit
) : BaseBottomSheet<BottomsheetFilterMyconsultationBinding>() {

    private val itemAdapter by lazy { ItemAdapter<ConsultationFilterItem>() }
    private val fastAdapter by lazy { FastAdapter.with(itemAdapter) }

    override fun getUiBinding(): BottomsheetFilterMyconsultationBinding {
        return BottomsheetFilterMyconsultationBinding.inflate(cloneLayoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        val selection = fastAdapter.getSelectExtension()
        selection.isSelectable = true

        viewBinding?.run {
            btmshtFilterRecycler.let {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = fastAdapter
            }
        }
        val patientSelected = arguments?.getParcelable<PatientFamily>(EXTRA_CURRENT_PATIENT)

        arguments?.getParcelableArrayList<PatientFamily>(EXTRA_LIST_PATIENT)?.let { listFamily ->
            listFamily.map {
                itemAdapter.add(ConsultationFilterItem(it).apply {
                    setSelectedItem(it.id == patientSelected?.id)
                })
            }
        }

        initListener()
    }

    private fun initListener() {
        fastAdapter.addEventHook(FilterEventHook {
            callbackPatient.invoke(it)
            dismiss()
        })
    }

    companion object {

        private const val EXTRA_LIST_PATIENT = "ConsultationFilter.ListPatient"
        private const val EXTRA_CURRENT_PATIENT = "ConsultationFilter.CurrentPatient"

        fun newInstance(
            listPatient: ArrayList<PatientFamily>,
            patientSelected: PatientFamily?,
            callbackPatient: (PatientFamily?) -> Unit
        ): ConsultationFilterBottomSheet {
            return ConsultationFilterBottomSheet(callbackPatient).apply {
                val bundle = Bundle().apply {
                    putParcelableArrayList(EXTRA_LIST_PATIENT, listPatient)
                    putParcelable(EXTRA_CURRENT_PATIENT, patientSelected)
                }
                arguments = bundle
            }
        }
    }
}
