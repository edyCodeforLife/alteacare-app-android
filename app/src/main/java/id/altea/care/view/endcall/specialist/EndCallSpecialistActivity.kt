package id.altea.care.view.endcall.specialist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.domain.model.ConsultationDoctor
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.loadImage
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityEndCallSpesialisBinding


class EndCallSpecialistActivity : BaseActivityVM<ActivityEndCallSpesialisBinding, EndCallSpecialistVM>() {
    private val viewModel by viewModels<EndCallSpecialistVM> { viewModelFactory }
    private val textTimer : String? by lazy {
        intent.getStringExtra(KEY_COUNT_TIME)
    }
    private val consultationId by lazy {
        intent.getIntExtra(KEY_CONSULTATION_ID,-1)
    }
    private val router = EndCallSpecialistRouter()

    override fun observeViewModel(viewModel: EndCallSpecialistVM) {
        observe(viewModel.doctorLiveData, ::handleDoctorData)
    }

    override fun bindViewModel(): EndCallSpecialistVM  = viewModel

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityEndCallSpesialisBinding = ActivityEndCallSpesialisBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            endCallSpecialistTextTimer.text = textTimer
        }
        viewModel.getConsultationDetail(consultationId)
    }

    override fun initUiListener() {
        viewBinding?.run {
            textOrderInfo.onSingleClick().subscribe {
                router.openOrderBottomSheetMedicine(this@EndCallSpecialistActivity)
            }.disposedBy(disposable)

            endCallBtnPayment.onSingleClick().subscribe {
                router.openMyConsultation(this@EndCallSpecialistActivity,consultationId)
            }.disposedBy(disposable)

        }

    }

    private fun handleDoctorData(consultationDetail: ConsultationDetail?) {
        viewBinding?.run {
            consultationDetail?.let { mConsultationDetail ->
                imageEndCallDoctorSpecialist.loadImage(mConsultationDetail.doctor?.photo ?: "")
            }
        }

    }

    companion object {
        const val KEY_COUNT_TIME =  "KEY_COUNT_TIME"
        const val KEY_CONSULTATION_ID = "CONSULTATION_ID"
        fun createIntent(caller: Context, value : String,consultationId : Int): Intent {
            return Intent(caller, EndCallSpecialistActivity::class.java)
                .putExtra(KEY_COUNT_TIME,value)
                .putExtra(KEY_CONSULTATION_ID,consultationId)

        }
    }
}