package id.altea.care.view.doctordetail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import id.altea.care.R
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.data.request.AppointmentRequest
import id.altea.care.core.data.request.ScheduleRequest
import id.altea.care.core.domain.model.*
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.*
import id.altea.care.databinding.ActivityDoctorDetailBinding
import id.altea.care.databinding.ItemScheduleDateBinding
import id.altea.care.view.common.item.ErrorRetryItem
import id.altea.care.core.mappers.Mapper.mapToConsultation
import id.altea.care.view.doctordetail.failure.DoctorDetailFailure
import id.altea.care.view.doctordetail.item.*
import id.altea.care.view.doctordetail.item.model.ScheduleDayItemModel
import id.altea.care.view.specialistsearch.model.FilterActiveModel
import kotlinx.android.synthetic.main.dialog_confirmation_reconsultation.view.*
import kotlinx.android.synthetic.main.dialog_delete_img_profile.view.*
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

/**
 * Created by trileksono on 10/03/21.
 */
class DoctorDetailActivity : BaseActivityVM<ActivityDoctorDetailBinding, DoctorDetailVM>() {

    @Inject lateinit var analyticManager: AnalyticManager

    private val itemAdapter by lazy { ItemAdapter<IItem<*>>() }
    private val fastAdapter by lazy { FastAdapter.with(itemAdapter) }
    private val router by lazy { DoctorDetailRouter() }
    private val doctor by lazy { intent.getParcelableExtra(EXTRA_DOCTOR) as Doctor? }
    private val refId by lazy { intent.getIntExtra(EXTRA_REF_ID, -1) }
    private lateinit var dateParam: String
    private var doctorSchedule: DoctorSchedule? = null
    private val consulMethod by lazy { intent.getStringExtra(EXTRA_CONSUL_METHOD) }
    private val patientId by lazy { intent.getStringExtra(EXTRA_PATIENT_ID) } // patien id for reconsultation
    private val doctorId by lazy { intent.getStringExtra(EXTRA_DOCTOR_ID) }
    private var filterActive  : FilterActiveModel.FilterDate? = null

    override fun observeViewModel(viewModel: DoctorDetailVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.isLoadingTimeSlot, ::handleLoadingTimeSlot)
        observe(viewModel.weekListLiveData, ::handleGenerateDays)
        observe(viewModel.timeListLiveData, ::handleTimeSlotInitial)
        observe(viewModel.txtSetDateEvent, ::handleTextTxtChangeDate)
        observe(viewModel.isUserLoggedIn, ::handleOpenIsUserLoggedIn)
        observe(viewModel.doctorEvent, ::handleDoctorDetailResponse)
        observe(viewModel.appointmentEvent, ::handleAppointmentEvent)
        observe(viewModel.termRefundEvent, {
            it?.let { viewBinding?.doctorDetailTxtTerm?.loadData(it, "text/html", "UTF-8") }
        })
        observe(viewModel.operationalHourMA, ::handleOperationalHourMA)
    }

    private fun handleOperationalHourMA(operationalHourMA: OperationalHourMA?) {
        showDialogReConsultation(this,operationalHourMA)
    }

    override fun bindViewModel(): DoctorDetailVM {
        return ViewModelProvider(this, viewModelFactory)[DoctorDetailVM::class.java]
    }

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.doctorDetailToolbar?.toolbar

    override fun getUiBinding(): ActivityDoctorDetailBinding {
        return ActivityDoctorDetailBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            doctorDetailRecycler.let {
                it.layoutManager = LinearLayoutManager(this@DoctorDetailActivity)
                it.adapter = fastAdapter
            }
            val toolbarParams =
                doctorDetailToolbar.root.layoutParams as CollapsingToolbarLayout.LayoutParams
            toolbarParams.collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
            doctorDetailToolbar.root.layoutParams = toolbarParams

            filterActive = intent.getParcelableExtra(EXTRA_FILTER_ACTIVE)

        }

        baseViewModel?.getDoctorDetail(doctor?.doctorId ?: doctorId.toString())
        generateDaysAndGetSchedule()
        if (refId != -1 && consulMethod != null) {
            doctor?.doctorId?.let {
                if (!it.isNullOrEmpty()){
                    baseViewModel?.getDoctorDetail(it)

                }else{
                   baseViewModel?.getDoctorDetail(doctorId.toString())
                }

            }
        }
    }

    private fun handleDoctorDetailResponse(doctor: Doctor?) {
        viewBinding?.run {
            doctor?.let {
                doctorDetailTxtProfile.loadData(it.abboutHtml ?: "", "text/html", "UTF-8")
                doctorDetailHeader.run {
                    doctorDetailTxtExperience.text = it.experience
                    doctorDetailTxtLanguage.text = it.overview
                }
                doctorDetailHeader.run {
                    val hospital: Hospital? =
                        if (!it.hospital.isNullOrEmpty()) it.hospital.get(0) else null
                    doctorDetailTxtExperience.text = it.experience
                    doctorDetailTxtName.text = it.doctorName
                    doctorDetailTxtTitle.text = it.specialization?.name
                    hospital?.let {
                        doctorDetailTxtHospitalName.text = it.name
                        doctorDetailImgHospitalIcon.loadImage(it.icon ?: "")
                    }

                    doctorDetailTxtLanguage.text = it.overview
                    doctorDetailImgDoctor.loadImage(it.photo ?: "")
                    doctorDetailTxtProfile.loadData(it.abboutHtml ?: "", "text/html", "UTF-8")

                    baseViewModel?.sendEventDoctorProfileToAnalytics(doctor)
                }
            }
        }
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is NotFoundFailure.DataNotExist -> itemAdapter.add(ScheduleEmptyItem())
            is DoctorDetailFailure.ScheduleUnloadFailure -> itemAdapter.add(ErrorRetryItem {
                baseViewModel?.getScheduleDoctor(doctor?.doctorId ?: doctorId.toString(), dateParam)
            })
            else -> super.handleFailure(failure)
        }
    }

    private fun generateDaysAndGetSchedule() {
        dateParam = Date().formatDate("yyyy-MM-dd")
        val dateFilterActive = if (filterActive == null){ dateParam }else{filterActive?.date ?: dateParam}
        baseViewModel?.run {
            changeStateTxtChangeDate(false) // set default text set date
            generateDayList(dateParam) // generate day one week ahead
            getScheduleDoctor(doctor?.doctorId ?: doctorId.toString(), dateFilterActive) // get schedule doctor
        }
    }

    private fun handleLoadingTimeSlot(showLoading: Boolean?) {
        if (showLoading == true) {
            itemAdapter.removeRange(1, itemAdapter.adapterItemCount - 1)
            itemAdapter.add(ScheduleLoadingItem())
        } else {
            itemAdapter.remove(itemAdapter.adapterItemCount - 1)
        }
    }

    private fun handleGenerateDays(days: List<Long>?) {
        // if txtSetDate.text == (atur tanggal or set date)
        // generate name of day
        if (baseViewModel?.txtSetDateEvent?.value == false) {
            itemAdapter.clear()
            days?.map {
                ScheduleDayItemModel(
                    it.getDateFromLong("EEEE", true),
                    it.getDateFromLong("yyyy-MM-dd", false)
                )
            }?.also { listSchedule ->
                // listener after click name of days
                // it will be call api for schedule date selected
                itemAdapter.add(ScheduleDaysItemList {
                    dateParam = it.dateParam
                    baseViewModel?.getScheduleDoctor(doctor?.doctorId ?: doctorId.toString(), dateParam)
                }.apply { applyChip(listSchedule,filterActive) })
            }
        } else {
            // if txtSetDate.text == (hapus or delete)
            // it will be generate date
            itemAdapter.clear()
            days?.map {
                itemAdapter.add(
                    ScheduleDateItem(it.getDateFromLong("EEEE, dd MMMM yyyy", true))
                )
            }
        }
    }

    private fun handleTimeSlotInitial(days: List<DoctorSchedule>?) {
        viewBinding?.doctorDetailBtnNext?.isEnabled = false

        // if txtSetDate.text == (atur tanggal or set date)
        // add schedule to recycler view
        if (baseViewModel?.txtSetDateEvent?.value == false) {
            itemAdapter.add(ScheduleTimeItemList {
                doctorSchedule = it
                viewBinding?.doctorDetailBtnNext?.isEnabled = true
            }.also { it.applyChip(days ?: listOf()) })
        } else {
            // if txtSetDate.text == (hapus or delete)
            // add date one week ahead into recycler view
            router.openScheduleBottomSheet(
                this@DoctorDetailActivity,
                if (!days.isNullOrEmpty()) days[0].date?.toNewFormat(
                    "yyyy-MM-dd",
                    "EEEE, dd MMMM yyyy",
                    true
                ).orEmpty() else "",
                days ?: emptyList()
            ) { doctorScheduleCallback ->
                doctorSchedule = doctorScheduleCallback
                baseViewModel?.nextClicked()
            }
        }
    }

    private fun handleCallbackChooseDate(date: String) {
        baseViewModel?.changeStateTxtChangeDate(true)
        baseViewModel?.generateDayList(date.toNewFormat(newFormat = "yyyy-MM-dd"))
    }

    private fun handleTextTxtChangeDate(isTextDeleteDate: Boolean?) {
        viewBinding?.run {
            if (isTextDeleteDate == true) {
                doctorDetailTxtSetDate.let {
                    it.text = getString(R.string.delete)
                    it.setTextColor(ContextCompat.getColor(this@DoctorDetailActivity, R.color.red))
                }
            } else {
                doctorDetailTxtSetDate.let {
                    it.text = getString(R.string.select_date)
                    it.setTextColor(
                        ContextCompat.getColor(
                            this@DoctorDetailActivity,
                            R.color.primary
                        )
                    )
                }
            }
            doctorDetailBtnNext.isEnabled = false
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            doctorDetailAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                doctorDetailAppBar.post {
                    try {
                        if (abs(verticalOffset) >= doctorDetailAppBar.totalScrollRange) {
                            doctorDetailToolbar.let {
                                it.doctorToolbarTxtName.text =
                                    doctorDetailHeader.doctorDetailTxtName.text.toString()
                                it.doctorToolbarTxtSpecialist.text =
                                    doctorDetailHeader.doctorDetailTxtTitle.text.toString()
                            }
                        } else {
                            doctorDetailToolbar.let {
                                it.doctorToolbarTxtName.text = " "
                                it.doctorToolbarTxtSpecialist.text = " "
                            }
                        }
                    } catch (ex: IllegalStateException) {
                        println(ex.localizedMessage)
                    }
                }
            })

            doctorDetailTxtSetDate
                .onSingleClick()
                .subscribe {
                    if (baseViewModel?.txtSetDateEvent?.value == true) {
                        generateDaysAndGetSchedule()
                        baseViewModel?.changeStateTxtChangeDate(false)
                    } else {
                        router.openDateBottomSheet(
                            this@DoctorDetailActivity,
                            ::handleCallbackChooseDate
                        )
                    }
                }
                .disposedBy(disposable)

            doctorDetailBtnNext
                .onSingleClick()
                .subscribe { baseViewModel?.nextClicked() }
                .disposedBy(disposable)
        }

        fastAdapter.addEventHook(object : ClickEventHook<IItem<*>>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return viewHolder.asBinding<ItemScheduleDateBinding> { it.root }
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<IItem<*>>,
                item: IItem<*>
            ) {
                doctor?.let {
                    baseViewModel?.getScheduleDoctorBottomSheet(
                        it.doctorId!!,
                        (item as ScheduleDateItem).sortType.toNewFormat(
                            "EEEE, dd MMMM yyyy",
                            "yyyy-MM-dd",
                            true
                        )
                    )
                }
            }
        })
    }

    private fun handleOpenIsUserLoggedIn(isLoggedIn: Boolean?) {
        if (isLoggedIn == true) {
            if (refId != -1 && consulMethod != null) {
                baseViewModel?.getOperationalHourMA()

            } else {
                openConsultation()
            }
        } else {
            doctor?.let { router.openGuestLogin(this, it) }
        }
    }

    private fun openConsultation() {
        router.openFamilyMemberListBottomSheet(this, doctor?.mapToConsultation(doctorSchedule))
    }

    private fun showDialogReConsultation(caller: Context,operationalHourMA: OperationalHourMA?) {
        val dialogView =
            LayoutInflater.from(caller).inflate(R.layout.dialog_confirmation_reconsultation, null)
        val alertDialogView = AlertDialog.Builder(caller).create()
        alertDialogView.setView(dialogView)
        alertDialogView.window?.apply {
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialogView.dialogReconsulMa.setOnClickListener {
            baseViewModel?.reCreateAppointment(
                doctor?.doctorId ?: doctorId.toString(),
                patientId.orEmpty(),
                consulMethod!!,
                ScheduleRequest(
                    doctorSchedule?.code,
                    doctorSchedule?.date,
                    doctorSchedule?.startTime,
                    doctorSchedule?.endTime
                ),
                AppointmentRequest.NEXT_STEP_MA,
                refId.toString(),
                operationalHourMA
            )
            alertDialogView.dismiss()
        }
        dialogView.dialogReconsulSpecialist.setOnClickListener {
            baseViewModel?.reCreateAppointment(
                doctor?.doctorId ?: doctorId.toString(),
                patientId.orEmpty(),
                consulMethod!!,
                ScheduleRequest(
                    doctorSchedule?.code,
                    doctorSchedule?.date,
                    doctorSchedule?.startTime,
                    doctorSchedule?.endTime
                ),
                AppointmentRequest.NEXT_STEP_DIRECT,
                refId.toString(),
                operationalHourMA
            )
            alertDialogView.dismiss()
        }
        alertDialogView.show()
    }

    private fun handleAppointmentEvent(triple: Triple<Appointment, String,OperationalHourMA?>?) {
        if (triple?.second == AppointmentRequest.NEXT_STEP_MA) {
            if (triple.first.inOperationalHour == false){
                router.openMaOperationalHourBottomSheet(supportFragmentManager,triple.third?.operationalHour.orEmpty())
            }else {
                router.openTransition(
                    this,
                    triple.first.appointmentId ?: -1,
                    baseViewModel?.profile?.toUserProfile() ?: UserProfile("ma", "email"),
                    triple.first.orderCode ?: "",
                    InfoDetail(
                        doctor?.doctorName,
                        doctor?.specialization?.name,
                        doctorSchedule?.date?.toNewFormat("yyyy-MM-dd", "EEEE, dd MMMM yyyy", true)
                    )
                )
            }
        } else {
            router.openPayment(this, doctor.mapToConsultation(doctorSchedule), triple?.first?.appointmentId ?: -1)
        }
    }

    companion object {
        private const val EXTRA_DOCTOR = "DoctorDetail.doctor"
        private const val EXTRA_REF_ID = "DoctorDetail.refAppointmentId"
        private const val EXTRA_CONSUL_METHOD = "DoctorDetail.ConsulMethod"
        private const val EXTRA_PATIENT_ID = "DoctorDetail.PatientId"
        private const val EXTRA_FILTER_ACTIVE = "DoctorDetail.FilterActive"
        private const val EXTRA_DOCTOR_ID = "DoctorDetail.doctorId"
        fun createIntent(
            caller: Context,
            doctor: Doctor? = null,
            refId: Int? = null,
            consulMethod: String? = null,
            patientId: String? = null,
            filterActiveModel: FilterActiveModel.FilterDate?=null,
            doctorId : String? = null
        ): Intent {
            return Intent(caller, DoctorDetailActivity::class.java)
                .putExtra(EXTRA_DOCTOR_ID,doctorId)
                .putExtra(EXTRA_DOCTOR, doctor)
                .putExtra(EXTRA_REF_ID, refId)
                .putExtra(EXTRA_CONSUL_METHOD, consulMethod)
                .putExtra(EXTRA_PATIENT_ID, patientId)
                .putExtra(EXTRA_FILTER_ACTIVE,filterActiveModel)
        }
    }
}
