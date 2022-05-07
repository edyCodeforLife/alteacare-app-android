package id.altea.care.view.consultation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import id.altea.care.R
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.event.ChangePatienCallbackEvent
import id.altea.care.core.domain.model.Consultation
import id.altea.care.core.domain.model.DetailPatient
import id.altea.care.core.domain.model.DoctorSchedule
import id.altea.care.core.domain.model.General
import id.altea.care.core.ext.*
import id.altea.care.core.helper.RxBus
import id.altea.care.databinding.FragmentCreateConsultationBinding
import id.altea.care.view.consultation.ConsultationRouter.Companion.KEY_CONSULTATION
import kotlinx.android.synthetic.main.content_patient_data.*

class ConsultationFragment : BaseFragmentVM<FragmentCreateConsultationBinding, ConsultationFragmentVM>() {

    private var dataConsultation: Consultation? = null

    private var bundle: Bundle? = null

    private val router by lazy { ConsultationRouter() }

    override fun getUiBinding(): FragmentCreateConsultationBinding {
        return FragmentCreateConsultationBinding.inflate(layoutInflater)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @SuppressLint("SetTextI18n")
    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        bundle = Bundle()
        dataConsultation = (activity as ConsultationActivity).dataConsultation
        val patientFamily = (activity as ConsultationActivity).patientFamily

        patientFamily?.id?.let { patientId ->
            viewModel?.getFamilyDetail(patientId)
        }

        disposable.delay(200){
            if (dataConsultation?.idDoctor?.isNotEmpty() == true){
                viewModel?.doRequestDoctorDetail(
                    doctorId = dataConsultation?.idDoctor ?: "",
                    DoctorSchedule(
                        code = dataConsultation?.codeSchedule,
                        date = dataConsultation?.dateSchedule,
                        startTime = dataConsultation?.startTime,
                        endTime = dataConsultation?.endTime,
                        schedule = ""
                    )
                )
            }
        }

        viewBinding?.run {
            // todo event listener to confirmation fragment
            consulBtnCreate.setOnClickListener {
                (activity as ConsultationActivity).onNext()
                bundle?.let {
                    it.putString(KEY_CONSULTATION_METHOD, VIDEO_CALL)
                    setFragmentResult(KEY_CONSULTATION, it)
                }
            }
            // todo set video call is default {hide voice call base on request management}
            consultationRdVideoCall.isChecked = true
            consulBtnCreate.isEnabled = true
        }
        bundle?.putParcelable(KEY_DATA_CONSULTATION, dataConsultation)
    }

    override fun onReExecute() {}

    override fun initUiListener() {
        viewBinding?.run {
            consultationRdGroupChoose.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.consultationRdVideoCall -> {
                        consulBtnCreate.isEnabled = true
                        bundle?.putString(KEY_CONSULTATION_METHOD, VIDEO_CALL)
                    }
                    R.id.consultationRdVoiceCall -> {
                        consulBtnCreate.isEnabled = true
                        bundle?.putString(KEY_CONSULTATION_METHOD, VOICE_CALL)
                    }
                }
            }

            viewDoctor.consultationTxtChange.onSingleClick()
                .subscribe {
                    router.openSearchSpecialist(
                        requireActivity(),
                        (activity as ConsultationActivity).dataConsultation?.specializationId
                    )
                }.disposedBy(disposable)

            consultationChangePatientText.onSingleClick().subscribe {
                router.openFamilyMemberListBottomSheet(requireActivity(), dataConsultation, true)
            }.disposedBy(disposable)
        }
        listenRxBus()
    }

    private fun listenRxBus() {
        RxBus.getEvents().subscribe { any ->
            when (any) {
                is ChangePatienCallbackEvent -> {
                    viewModel?.getFamilyDetail(any.patientFamily.id ?: "")
                }
            }
        }.disposedBy(disposable)
    }

    override fun initMenu(): Int = 0

    override fun observeViewModel(viewModel: ConsultationFragmentVM) {
        observe(viewModel.detailPatientEvent, ::handleDetailPatient)
        observe(viewModel.generalEvent, ::getLogout)
        observe(viewModel.doctorDetailEvent, ::getDoctorDetail)
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
    }

    @SuppressLint("SetTextI18n")
    private fun handleDetailPatient(detailPatient: DetailPatient?) {
        detailPatient?.let {dataDetailPatient ->
            bundle?.putParcelable(KEY_DATA_PATIENT, detailPatient)
            viewBinding?.run {
                patientTxtName.text = "${dataDetailPatient.firstName} ${dataDetailPatient.lastName}"
                patientTxtAge.text = String.format(
                    getString(R.string.s_age),
                    dataDetailPatient.age?.year ?: "0",
                    dataDetailPatient.age?.month ?: "0"
                )
                patientTxtBirthDate.text = dataDetailPatient.birthDate?.toNewFormat("yyyy-MM-dd", "dd/MM/yyyy")
                patientTxtGender.text = dataDetailPatient.gender
                patientTxtPhone.text = dataDetailPatient.phone
                patientTxtEmail.text = dataDetailPatient.email
            }
        }
    }

    override fun bindViewModel(): ConsultationFragmentVM {
        return ViewModelProvider(this, viewModelFactory).get(ConsultationFragmentVM::class.java)
    }

    private fun getLogout(general: General?) {
        if (general?.status == true) {
            router.openGuestLogin(
                requireActivity(),
                (activity as ConsultationActivity).dataConsultation
            )
        }
    }

    private fun getDoctorDetail(doctor: Consultation?)  {
        if (doctor != null){
            dataConsultation = doctor
            setupUI(doctor)
        }else{
            setupUI(dataConsultation)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(dataDoctor : Consultation?) = viewBinding?.run {
        viewDoctor.run {
            consultationTxtChange.isEnabled = true
            dataDoctor?.let { dataConsultation ->
                consultationImgDoctor.loadImage(dataConsultation.imgDoctor)
                consultationImgRs.loadImage(dataConsultation.iconRs)
                consultationTxtHospital.text = dataConsultation.nameRs
                consultationTxtNameDoctor.text = dataConsultation.nameDoctor
                consultationTxtPrice.text = dataConsultation.priceDoctor
                consultationTxtSpesialis.text = dataConsultation.specialistDoctor
                consultationTxtCalendar.text = dataConsultation.dateSchedule.toNewFormat("yyyy-MM-dd", "EEEE, dd MMMM yyyy", true)
                consultationTxtTime.text = "${dataConsultation.startTime}-${dataConsultation.endTime}"
                if (dataConsultation.priceStrikeDecimal > 0 && dataConsultation.priceDoctorDecimal != 0L) {
                    consultationTxtStrikePrice.visibility = View.VISIBLE
                    consultationTxtStrikePrice.text = dataConsultation.priceStrike
                    consultationTxtStrikePrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    consultationTxtStrikePrice.visibility = View.INVISIBLE
                }
            }
        }
    }

    companion object {
        const val VIDEO_CALL = "VIDEO_CALL"
        const val VOICE_CALL = "VOICE_CALL"
        const val KEY_DATA_PATIENT = "KEY_BUNDLE_DATA_PATIENT"
        const val KEY_CONSULTATION_METHOD = "KEY_BUNDLE_CONSULTATION_METHOD"
        const val KEY_DATA_CONSULTATION = "KEY_DATA_CONSULTATION"
    }
}
