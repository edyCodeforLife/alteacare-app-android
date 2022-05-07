package id.altea.care.view.consultation.confirm

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import id.altea.care.R
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.BookingSchedulePayloadBuilder
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.data.request.AppointmentRequest
import id.altea.care.core.data.request.ScheduleRequest
import id.altea.care.core.domain.event.ChangePatienCallbackEvent
import id.altea.care.core.domain.model.*
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.*
import id.altea.care.core.helper.RxBus
import id.altea.care.databinding.FragmentConfirmationBinding
import id.altea.care.view.consultation.ConsultationActivity
import id.altea.care.view.consultation.ConsultationFragment.Companion.KEY_CONSULTATION_METHOD
import id.altea.care.view.consultation.ConsultationFragment.Companion.KEY_DATA_CONSULTATION
import id.altea.care.view.consultation.ConsultationFragment.Companion.KEY_DATA_PATIENT
import id.altea.care.view.consultation.ConsultationFragment.Companion.VIDEO_CALL
import id.altea.care.view.consultation.ConsultationFragment.Companion.VOICE_CALL
import id.altea.care.view.consultation.ConsultationRouter.Companion.KEY_CONSULTATION
import id.altea.care.view.login.LoginActivityRouter
import kotlinx.android.synthetic.main.content_patient_data_consultation.*
import kotlinx.android.synthetic.main.fragment_confirmation.*
import kotlinx.android.synthetic.main.item_confirmation_doctor.*
import permissions.dispatcher.*
import java.util.*
import javax.inject.Inject

@RuntimePermissions
class ConfirmationFragment : BaseFragmentVM<FragmentConfirmationBinding, ConfirmationFragmentVM>() {

    @Inject lateinit var analyticManager: AnalyticManager

    private val router = ConfirmationRouter()
    private val schedules = ArrayList<ScheduleRequest>()
    private var appointment: AppointmentRequest? = null
    private var detailPatient: DetailPatient? = null
    private var consultationMethod: String? = ""
    private var dataConsultation: Consultation? = null

    private var patientId: String? = ""
    private var doctorId: String? = ""

    override fun observeViewModel(viewModel: ConfirmationFragmentVM) {
        observe(viewModel.appointmentResult, ::getResultAppointment)
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.generalEvent, ::getLogout)
        observe(viewModel.operationalHourMaEvent, ::handleOperationalHourMA)
    }

    override fun bindViewModel(): ConfirmationFragmentVM {
        return ViewModelProvider(this, viewModelFactory).get(ConfirmationFragmentVM::class.java)
    }

    override fun getUiBinding(): FragmentConfirmationBinding {
        return FragmentConfirmationBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        viewBinding?.run {
            setFragmentResultListener(KEY_CONSULTATION) { _, bundle ->

                detailPatient = bundle.getParcelable(KEY_DATA_PATIENT)
                patientId = detailPatient?.id

                consultationMethod = bundle.getString(KEY_CONSULTATION_METHOD, "NOT DEFINED")
                dataConsultation = bundle.getParcelable(KEY_DATA_CONSULTATION)

                when (consultationMethod) {
                    VIDEO_CALL -> {
                        txtStatusAppoiment.text = getString(R.string.str_video_call)
                        txtStatusAppoiment.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_video_rd,
                            0,
                            0,
                            0
                        )
                    }
                    VOICE_CALL -> {
                        txtStatusAppoiment.text = getString(R.string.str_voice_call)
                        txtStatusAppoiment.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ic_call_method,
                            0,
                            0,
                            0
                        )
                    }
                    else -> {}
                }

                detailPatient?.let {
                    patientTxtName.text = "${it.firstName} ${it.lastName}"
                    patientTxtAge.text =
                        String.format(
                            getString(R.string.s_age),
                            it.age?.year ?: "0",
                            it.age?.month ?: "0"
                        )
                    patientTxtBirthDate.text = it.birthDate
                    patientTxtGender.text = it.gender
                    patientTxtPhone.text = it.phone
                    patientTxtEmail.text = it.email
                }

                dataConsultation?.let {
                    schedules.clear()
                    schedules.add(
                        ScheduleRequest(
                            it.codeSchedule,
                            it.dateSchedule,
                            it.startTime,
                            it.endTime
                        )
                    )
                    doctorId = it.idDoctor
                    appointment = AppointmentRequest(
                        doctorId = doctorId,
                        patientId = patientId,
                        consultationMethod = consultationMethod,
                        schedules = schedules
                    )

                    lnrLayoutConfimDoctor.run {
                        confirmationImgDoctor.loadImage(it.imgDoctor)
                        confirmationImgRs.loadImage(it.iconRs)
                        confirmationTxtHospital.text = it.nameRs
                        confirmationTxtNameDoctor.text = it.nameDoctor
                        confirmationTxtSpesialis.text = it.specialistDoctor
                        confirmationTxtCalendar.text =
                            it.dateSchedule.toNewFormat("yyyy-MM-dd", "EEEE, dd MMMM yyyy", true)
                        confirmationTxtTime.text = it.startTime + "-" + it.endTime
                    }
                    txtConfirmationPrice.text = it.priceDoctor

                    if (it.priceStrikeDecimal > 0 && it.priceDoctorDecimal != 0L) {
                        confirmationTxtStrikePrice.visibility = View.VISIBLE
                        confirmationTxtStrikePrice.text = it.priceStrike
                        confirmationTxtStrikePrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        confirmationTxtStrikePrice.visibility = View.INVISIBLE
                    }

                }
            }
        }
    }

    override fun onReExecute() {}

    private fun getLogout(general: General?) {
        if (general?.status == true) {
            router.openGuestLogin(
                requireActivity(),
                (activity as ConsultationActivity).dataConsultation
            )
        }
    }

    override fun initUiListener() {
        viewBinding.run {
            confirmBtnAccept.onSingleClick().subscribe {
                openBottomSheetWithPermissionCheck()
            }.disposedBy(disposable)

            consulBtnChange.onSingleClick().subscribe {
                router.openFamilyMemberListBottomSheet(
                    requireActivity(),
                    dataConsultation,
                    true)
            }.disposedBy(disposable)

            listenRxBus()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun listenRxBus() {
        RxBus.getEvents().subscribe { any ->
            when(any) {
                is ChangePatienCallbackEvent -> {
                    patientId = any.patientFamily.id
                    patientTxtName.text = "${any.patientFamily.firstName} ${any.patientFamily.lastName}"
                    patientTxtAge.text =
                        String.format(
                            getString(R.string.s_age),
                            any.patientFamily.age?.year ?: "0",
                            any.patientFamily.age?.month ?: "0"
                        )
                    patientTxtBirthDate.text = any.patientFamily.birthDate
                    patientTxtGender.text = any.patientFamily.gender
                    patientTxtPhone.text = any.patientFamily.phone
                    patientTxtEmail.text = any.patientFamily.email

                    appointment = AppointmentRequest("", "", "", listOf())
                    appointment = AppointmentRequest(
                        doctorId = doctorId,
                        patientId = patientId,
                        consultationMethod = consultationMethod,
                        schedules = schedules
                    )
                }
            }
        }.disposedBy(disposable)
    }

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun openBottomSheet() {
        router.openConfirmCallBottomSheet(activity?.supportFragmentManager, {
            if (viewModel?.appointmentResult?.value != null) {
                getResultAppointment(viewModel?.appointmentResult?.value)
                return@openConfirmCallBottomSheet
            }
            appointment?.let { appointmentRequest ->
                viewModel?.createAppointment(appointmentRequest)
            }
        }, {

        })
        sendEventBookingScheduleToAnalytics()
    }

    private fun sendEventBookingScheduleToAnalytics() {
        dataConsultation?.let { mDataConsultation ->
            analyticManager.trackingEventBookingSchedule(
                BookingSchedulePayloadBuilder(
                    mDataConsultation.hospitalId,
                    mDataConsultation.nameRs,
                    mDataConsultation.bookingDate,
                    mDataConsultation.bookingHour,
                    mDataConsultation.idDoctor,
                    mDataConsultation.nameDoctor,
                    mDataConsultation.specialistDoctor
                )
            )
        }
    }

    private fun getResultAppointment(appointment: Appointment?) {
        viewModel?.getOperationalHourMA(appointment)
    }

    private fun gotoTransitionVideo(idAppointment: Int?,orderCode : String?) {
        idAppointment?.let { appointmentId ->
            (activity as ConsultationActivity).dataConsultation?.let {
                detailPatient?.let {
                    router.openTransitionVideoActivity(
                        requireContext(), appointmentId,
                        orderCode,
                        it.toUserProfile(),
                        InfoDetail(
                            dataConsultation?.nameDoctor, dataConsultation?.specialistDoctor,
                            dataConsultation?.dateSchedule?.toNewFormat(
                                "yyyy-MM-dd",
                                "EEEE, dd MMMM yyyy",
                                true
                            )
                        )
                    )
                }
            }
        }
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection ->
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_disconnect),
                    Toast.LENGTH_SHORT
                ).show()
            is Failure.ServerError -> Toast.makeText(
                requireContext(),
                failure.message,
                Toast.LENGTH_SHORT
            ).show()
            is Failure.ExpiredSession -> {
                startActivity(
                    LoginActivityRouter.createIntent(requireContext())
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                )
                requireActivity().finish()
            }
            else -> Toast.makeText(
                requireContext(),
                getString(R.string.error_default),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @OnShowRationale(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun showRationale(request: PermissionRequest) {
        showRationaleDialog(requireContext(), R.string.permission_call_deny, request)
    }

    @OnPermissionDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onPermissionDenied() {
        openBottomSheetWithPermissionCheck()
    }

    @OnNeverAskAgain(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onCameraAndStorageNeverAskAgain() {
        showPermissionSettingDialog(requireActivity(), R.string.permission_call_deny)
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    fun onChange() {
        showDialogChangePatient(this.requireContext()) {
            viewModel?.doLogout()
        }
    }

    override fun initMenu(): Int = 0

    private fun handleOperationalHourMA(dataOperationalHoursMA : Pair<OperationalHourMA, Appointment?>?) {
        dataOperationalHoursMA?.let { data ->
            if (data.second?.inOperationalHour == false){
                router.openMaOperationalHourBottomSheet(activity?.supportFragmentManager,data.first.operationalHour.orEmpty())
            }else {
                gotoTransitionVideo(data.second?.appointmentId,data.second?.orderCode)
            }
        }
    }
}
