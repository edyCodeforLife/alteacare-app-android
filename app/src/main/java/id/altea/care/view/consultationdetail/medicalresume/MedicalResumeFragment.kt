package id.altea.care.view.consultationdetail.medicalresume

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.MedicalNotesPayloadBuilder
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.model.ConsulMedicalResume
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.FragmentMedicalResumeBinding
import id.altea.care.view.consultationdetail.ConsultationDetailActivity
import id.altea.care.view.consultationdetail.ConsultationDetailSharedVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.content_empty_medical_resume.view.*
import kotlinx.android.synthetic.main.fragment_medical_resume.*
import timber.log.Timber
import javax.inject.Inject

class MedicalResumeFragment : BaseFragmentVM<FragmentMedicalResumeBinding, ConsultationDetailSharedVM>() {

    @Inject
    lateinit var analyticManager: AnalyticManager

    private var consultationId: Int? = 0
    private var isLoading = false

    override fun observeViewModel(viewModel: ConsultationDetailSharedVM) {
        observe(viewModel.failureLiveData, ::handleFailure)

        viewModel.isShowLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoading = it
                handleLoadingLivedataEvent(isLoading)
            }
            .disposedBy(disposable)

        viewModel.appointmentDetail
            .subscribe(
                { getDataMedicalResume(it.medicalResume) },
                { Timber.e(it) })
            .disposedBy(disposable)
    }

    override fun onResume() {
        super.onResume()
        handleLoadingLivedataEvent(isLoading)
    }

    override fun onPause() {
        super.onPause()
        handleLoadingLivedataEvent(false)
    }

    override fun bindViewModel(): ConsultationDetailSharedVM {
        return ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[ConsultationDetailSharedVM::class.java]
    }

    override fun getUiBinding(): FragmentMedicalResumeBinding {
        return FragmentMedicalResumeBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        consultationId = (activity as ConsultationDetailActivity).consultationId
    }

    private fun getDataMedicalResume(consulMedicalResume: ConsulMedicalResume?) {
        viewBinding?.let {
            if (consulMedicalResume?.symptom != null) {
                medicalResumeContentEmpty.visibility = View.GONE
                consulMedicalResume.let { consulMedicalResume ->
                    it.medicalResumeTxtComplaint.text = consulMedicalResume.symptom?.replace("\\\n",System.getProperty("line.separator")) ?: "-"
                    it.medicalResumeTxtDiagnosis.text = consulMedicalResume.diagnosis ?: "-"
                    it.medicalResumeTxtMedicine.text = consulMedicalResume.drugResume ?: "-"
                    it.medicalResumeTxtSupportCheckup.text = consulMedicalResume.additionalResume ?: "-"
                    it.medicalResumeTxtConsultation.text = consulMedicalResume.consultation ?: "-"
                    it.medicalResumeTxtNote.text = consulMedicalResume.notes ?: "-"
                    sendEventMedicalNotesToAnalytics(consulMedicalResume)
                }
            } else {
                medicalResumeContentEmpty.visibility = View.VISIBLE
            }
        }
    }

    private fun sendEventMedicalNotesToAnalytics(consulMedicalResume: ConsulMedicalResume) {
        analyticManager.trackingEventMedicalNotes(
            MedicalNotesPayloadBuilder(
                consulMedicalResume.diagnosis,
                consulMedicalResume.symptom,
                consulMedicalResume.drugResume,
                consulMedicalResume.additionalResume,
                consulMedicalResume.notes,
            )
        )
    }

    override fun onReExecute() {}

    override fun initUiListener() {

        viewBinding?.let {
            medicalResumeContentEmpty.let {
                it.itemEmptyResumeTxtMessage.visibility = View.VISIBLE
                it.itemEmptyBtnRefresh.onSingleClick().subscribe {
                    viewModel?.getConsultationDetail(consultationId!!)
                }.disposedBy(disposable)
            }

            onMedicalResumeSwipe.setOnRefreshListener {
                viewModel?.getConsultationDetail(consultationId!!)
            }
        }

    }

    private fun handleLoadingLivedataEvent(showLoading: Boolean?) {
        viewBinding?.run { onMedicalResumeSwipe.isRefreshing = showLoading == true }
    }

    override fun initMenu(): Int = 0

    companion object {
        fun newInstance(): MedicalResumeFragment {
            return MedicalResumeFragment()
        }
    }
}
