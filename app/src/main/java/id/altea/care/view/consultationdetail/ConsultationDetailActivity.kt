package id.altea.care.view.consultationdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.android.AndroidInjection
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.databinding.ActivityConsultationDetailBinding
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.consultationdetail.consultationcost.ConsultationCostFragment
import id.altea.care.view.consultationdetail.medicaldocument.MedicalDocumentFragment
import id.altea.care.view.consultationdetail.medicalresume.MedicalResumeFragment
import id.altea.care.view.consultationdetail.patientdata.PatientDataFragment

class ConsultationDetailActivity :
    BaseActivityVM<ActivityConsultationDetailBinding, ConsultationDetailSharedVM>() {

    var consultationId: Int = -1
    private lateinit var typeAppointment: TypeAppointment
    private val viewModel by viewModels<ConsultationDetailSharedVM> { viewModelFactory }

    private val tabId by lazy {
        intent.getIntExtra(EXTRA_TAB_ID,0)
    }

    override fun observeViewModel(viewModel: ConsultationDetailSharedVM) {

    }

    override fun bindViewModel(): ConsultationDetailSharedVM {
        return viewModel
    }

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.toolbar

    override fun getUiBinding(): ActivityConsultationDetailBinding {
        AndroidInjection.inject(this)
        return ActivityConsultationDetailBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        typeAppointment = intent.getSerializableExtra(EXTRA_TYPE) as TypeAppointment
        consultationId = intent.getSerializableExtra(EXTRA_CONSULTATION_ID) as Int
        viewModel.getConsultationDetail(consultationId)
        viewBinding?.run {
            txtToolbarTitle.text = getString(R.string.consultation_detail)
            consulDetailViewPager.adapter = ConsultationDetailPagerAdapter(
                listOf(
                    PatientDataFragment.newInstance(typeAppointment, consultationId),
                    MedicalResumeFragment.newInstance(),
                    MedicalDocumentFragment.newInstance(),
                    ConsultationCostFragment.newInstance()
                ),
                supportFragmentManager,
                lifecycle
            )
            TabLayoutMediator(consulDetailTab, consulDetailViewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.detail_consultation_tab1)
                    1 -> tab.text = getString(R.string.detail_consultation_tab2)
                    2 -> tab.text = getString(R.string.detail_consultation_tab3)
                    3 -> tab.text = getString(R.string.detail_consultation_tab4)
                }
            }.attach()


            when (typeAppointment) {
                TypeAppointment.WAITING_FOR_MEDICAL_RESUME -> {
                    consulDetailViewPager.setCurrentItem(1, true)
                }
                TypeAppointment.PAID, TypeAppointment.COMPLETED ->
                    when (tabId) {
                        0 -> {
                            consulDetailViewPager.setCurrentItem(0, true)
                        }
                        1 -> {
                            consulDetailViewPager.setCurrentItem(1, true)
                        }
                        2 -> {
                            consulDetailViewPager.setCurrentItem(2, true)
                        }
                        3 -> {
                            consulDetailViewPager.setCurrentItem(3, true)
                        }
                    }
                TypeAppointment.WATCH_MEMO_ALTEA -> {
                    consulDetailViewPager.setCurrentItem(1, true)
                }
                else -> {
                }
            }

        }
    }

    override fun initUiListener() {

    }


    companion object {
        private const val EXTRA_TYPE = "ConsultationDetail.Type"
        private const val EXTRA_CONSULTATION_ID = "ConsultationDetail.ID"
        private const val EXTRA_TAB_ID = "ConsultationDetail.TabID"

        fun createIntent(
            context: Context,
            typeAppointment: TypeAppointment? =null,
            consultationId: Int?,
            tabId : Int? = 0
        ): Intent {
            return Intent(context, ConsultationDetailActivity::class.java).apply {
                putExtra(EXTRA_TYPE, typeAppointment)
                putExtra(EXTRA_CONSULTATION_ID, consultationId)
                putExtra(EXTRA_TAB_ID,tabId)
            }
        }
    }
}

