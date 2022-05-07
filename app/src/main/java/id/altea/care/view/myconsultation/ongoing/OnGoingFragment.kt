package id.altea.care.view.myconsultation.ongoing

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import id.altea.care.R
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.event.AppointmentStatusUpdateEvent
import id.altea.care.core.domain.event.MyConsultationFilterEvent
import id.altea.care.core.domain.model.*
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.*
import id.altea.care.core.helper.RxBus
import id.altea.care.core.helper.validator.TimeConversionValidator.Companion.istTimeConversion
import id.altea.care.databinding.FragmentOnGoingBinding
import id.altea.care.view.common.enums.TypeAppointment.*
import id.altea.care.view.common.item.AppointmentEmptyItem
import id.altea.care.view.common.item.ProgressBarItem
import id.altea.care.view.consultation.PageType
import id.altea.care.view.main.MainActivity
import id.altea.care.view.myconsultation.item.MyConsultationItem
import timber.log.Timber
import java.util.*
import javax.annotation.meta.When

class OnGoingFragment : BaseFragmentVM<FragmentOnGoingBinding, OnGoingFragmentVM>() {

    private lateinit var fastAdapter: GenericFastItemAdapter
    private lateinit var footerAdapter: GenericItemAdapter

    private val today = Date()
    private lateinit var thisWeek: String
    private val router by lazy { OnGoingRouter() }
    private lateinit var endlessScroll: EndlessRecyclerOnScrollListener
    private var doctor: ConsultationDoctor? = null
    private var isOperationalHour: Boolean? = false

    override fun getUiBinding(): FragmentOnGoingBinding {
        return FragmentOnGoingBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        initialCalendar()
        initRecyclerView()
        viewModel?.getOperationalHourMA()
        val dateMyConsultation = (activity as MainActivity).dateMyConsultation
        when(dateMyConsultation){
            "today" ->{
                viewBinding?.onGoingRbToday?.isChecked = true
            }
            "thisweek" ->{
                viewBinding?.onGoingRbThisWeek?.isChecked = true
            }
            "thismonth" ->{
                viewBinding?.onGoingRbThisMonth?.isChecked = true
            }
            else ->{
                viewBinding?.onGoingRbToday?.isChecked = true
            }
        }

        viewModel?.run {
            setFilterDate(
                today.formatDate(DEFAULT_DATE_FORMAT),
                today.formatDate(DEFAULT_DATE_FORMAT)
            )
            endlessScroll.resetPageCount(0)
        }

        listenRxBus()
    }

    private fun initialCalendar() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = today.time
        calendar.add(Calendar.DATE, 7)
        thisWeek = calendar.timeInMillis.getDateFromLong(format = DEFAULT_DATE_FORMAT)
    }

    private fun initRecyclerView() {
        fastAdapter = FastItemAdapter()
        footerAdapter = ItemAdapter.items()
        fastAdapter.addAdapter(1, footerAdapter)

        endlessScroll = object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                endlessScroll.disable()
                viewModel?.getOnGoingSchedule(currentPage + 1)
            }
        }.apply { disable() }

        viewBinding?.run {
            onGoingRecycler.let {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = fastAdapter
                it.addOnScrollListener(endlessScroll)
            }
        }
    }

    private fun listenRxBus() {
        RxBus.getEvents()
            .subscribe {
                when (it) {
                    is AppointmentStatusUpdateEvent -> endlessScroll.resetPageCount(0)
                    is MyConsultationFilterEvent.SelectedFilterEvent -> {
                        if (isResumed) {
                            viewModel?.setFilterPatient(it.patientFamily)
                            endlessScroll.resetPageCount(0)
                            if (it.patientFamily != null) {
                                viewBinding?.onGoingTxtFilter?.run {
                                    this.isVisible = true
                                    text = it.patientFamily.familyRelationType?.name
                                }
                                viewBinding?.text?.isVisible = true
                            } else {
                                viewBinding?.onGoingTxtFilter?.isVisible = false
                                viewBinding?.text?.isVisible = false
                            }
                        }
                    }
                }
            }
            .disposedBy(disposable)
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is NotFoundFailure.DataNotExist -> {
                fastAdapter.clear()
                fastAdapter.add(AppointmentEmptyItem(R.string.str_ongoing_empty))
                fastAdapter.notifyDataSetChanged()
            }
            is NotFoundFailure.EmptyListLoadMore -> endlessScroll.disable()
            else -> super.handleFailure(failure)
        }
    }

    override fun onReExecute() {}

    override fun initUiListener() {
        viewBinding?.onGoingRgFilter?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.onGoingRbToday -> {
                    viewModel?.setFilterDate(
                        today.formatDate(DEFAULT_DATE_FORMAT),
                        today.formatDate(DEFAULT_DATE_FORMAT)
                    )
                    endlessScroll.resetPageCount(0)
                }
                R.id.onGoingRbThisWeek -> {
                    viewModel?.setFilterDate(
                        today.getDateTomorrow(DEFAULT_DATE_FORMAT),
                        today.getDateNextWeek(DEFAULT_DATE_FORMAT)
                    )
                    endlessScroll.resetPageCount(0)
                }
                R.id.onGoingRbThisMonth -> {
                    viewModel?.setFilterDate(
                        today.getDateNextWeek(DEFAULT_DATE_FORMAT),
                        today.getDateNextMonth(DEFAULT_DATE_FORMAT)
                    )
                    endlessScroll.resetPageCount(0)
                }
            }
        }

        viewBinding?.onGoingImgFilter?.onSingleClick()
            ?.subscribe { RxBus.post(MyConsultationFilterEvent.OpenFilterEvent(viewModel?.patientFamily)) }
            ?.disposedBy(disposable)

        viewBinding?.onGoingSwipe?.setOnRefreshListener {
            viewModel?.getOperationalHourMA()
            endlessScroll.resetPageCount(0)
        }

        fastAdapter.onClickListener = { _, _, item, _ ->
            if (item is MyConsultationItem) {
                when (item.myAppointment.status) {
                    WAITING_FOR_PAYMENT -> {
                        item.myAppointment.let { myAppointment ->
                            val consultation = Consultation(
                                hospitalId = "",
                                iconRs = myAppointment.hospitalImage,
                                nameRs = myAppointment.hospitalName,
                                idDoctor = myAppointment.doctorId ?: "",
                                imgDoctor = myAppointment.doctorImage,
                                priceDoctor = (myAppointment.transaction?.total ?: 0).toString(),
                                nameDoctor = myAppointment.doctorName,
                                specialistDoctor = myAppointment.specialization,
                                codeSchedule = myAppointment.orderCode,
                                dateSchedule = myAppointment.scheduleDate,
                                startTime = myAppointment.scheduleStart,
                                endTime = myAppointment.scheduleEnd,
                                priceDoctorDecimal = (myAppointment.transaction?.total
                                    ?: 0).toLong(),
                                priceStrikeDecimal = (myAppointment.transaction?.total
                                    ?: 0).toLong(),
                                priceStrike = (myAppointment.transaction?.total ?: 0).toString(),
                                specializationId = "default"
                            )

                            if (item.myAppointment.transaction?.redirectUrl == null) {
                                router.openConsultation(
                                    requireActivity(),
                                    PageType.PAYMENT,
                                    consultation,
                                    myAppointment.orderCode,
                                    item.myAppointment.id
                                )
                            } else {
                                viewModel?.getRedirectUrl(item.myAppointment.transaction.redirectUrl)
                                    ?.let { url ->
                                        router.openPaymentInformation(
                                            requireActivity() as AppCompatActivity,
                                            item.myAppointment.id,
                                            url,
                                            item.myAppointment.orderCode
                                        )
                                    }
                            }
                        }
                    }
                    MEET_SPECIALIST, PAID, COMPLETED, WAITING_FOR_MEDICAL_RESUME -> {
                        item.myAppointment.status.let { typeAppointment ->
                            router.openConsultationDetail(
                                requireActivity(),
                                typeAppointment,
                                item.myAppointment.id
                            )
                        }
                    }

                    UNVERIFIED, NEW, PROCESS_GP -> {
                        handleListenerOperationalMA(item, viewModel?.operationalHourMA?.value)
                    }

                    ON_GOING -> {
                        router.openCallWithSpecialist(
                            requireActivity() as AppCompatActivity,
                            item.myAppointment.id, doctor,
                            InfoDetail(
                                item.myAppointment.doctorName, item.myAppointment.specialization,
                                item.myAppointment.scheduleDate.toNewFormat(
                                    "yyyy-MM-dd",
                                    "EEEE, dd MMMM yyyy",
                                    true
                                )
                            )
                        )
                    }
                    else -> {
                    }
                }
            }
            false
        }
    }

    override fun initMenu(): Int = 0

    private fun handleLoadingLivedata(showLoading: Boolean?) {
        viewBinding?.run { onGoingSwipe.isRefreshing = showLoading == true }
    }

    private fun handleResponseAppointment(appointmentList: List<AppointmentData>?) {
        appointmentList?.let {
            if (fastAdapter.itemCount > 0) {
                fastAdapter.clear()
            }

            fastAdapter.add(appointmentList.map { dataAppointment ->
                MyConsultationItem(AppointmentData.mapToMyAppointment(dataAppointment),isOperationalHour)
            })
            appointmentList.map { dataAppointment ->
                doctor = ConsultationDoctor(
                    id = dataAppointment.doctor?.doctorId,
                    name = dataAppointment.doctor?.doctorName,
                    photo = dataAppointment.doctor?.photo,
                    specialist = dataAppointment.doctor?.specialization,
                    hospital = null
                )
            }

            viewBinding?.onGoingRecycler?.postDelayed(500) {
                fastAdapter.notifyDataSetChanged()
            }
            endlessScroll.enable()
        }
    }

    private fun handleResponseLoadMore(appointmentList: List<AppointmentData>?) {
        appointmentList?.let { letAppointmentList ->
            if (letAppointmentList.isEmpty()) endlessScroll.disable()
            fastAdapter.add(appointmentList.map { dataAppointment ->
                MyConsultationItem(AppointmentData.mapToMyAppointment(dataAppointment),isOperationalHour)
            })
        }
    }

    private fun handleLoadingLoadMore(showLoading: Boolean?) {
        if (showLoading == true && fastAdapter.itemCount > 0) {
            footerAdapter.add(ProgressBarItem())
        } else {
            footerAdapter.clear()
        }
    }

    override fun observeViewModel(viewModel: OnGoingFragmentVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoadingLivedata)
        observe(viewModel.appointmentListLiveEvent, ::handleResponseAppointment)
        observe(viewModel.isLoadingLoadMore, ::handleLoadingLoadMore)
        observe(viewModel.appointmentListLoadMoreEvent, ::handleResponseLoadMore)
        observe(viewModel.operationalHourMA, ::handleOperationalMA)
    }

    private fun handleOperationalMA(dataOperationalHourMA: OperationalHourMA?) {
        dataOperationalHourMA?.let { letOperationalMa ->
            Timber.d("data Ma $letOperationalMa")
            isOperationalHour = istTimeConversion(
                    letOperationalMa.operationalHourStart.orEmpty(),
                    letOperationalMa.operationalHourEnd.orEmpty()
                )
        }
    }

    private fun handleListenerOperationalMA(
        myConsultationItem: MyConsultationItem,
        operationalHourMA: OperationalHourMA?
    ) {
        operationalHourMA?.let { dataOperationalMA ->
            if (!istTimeConversion(
                    dataOperationalMA.operationalHourStart.orEmpty(),
                    dataOperationalMA.operationalHourEnd.orEmpty()
                )
            ) {
                router.openMaOperationalHourBottomSheet(
                    fragmentManager = activity?.supportFragmentManager,
                    operationalHour = dataOperationalMA.operationalHour.orEmpty()
                )
            } else {
                router.openReconsultationWithMA(
                    requireActivity() as AppCompatActivity,
                    myConsultationItem.myAppointment.id,
                    myConsultationItem.myAppointment.orderCode,
                    UserProfile(myConsultationItem.myAppointment.patient?.name),
                    InfoDetail(
                        myConsultationItem.myAppointment.doctorName,
                        myConsultationItem.myAppointment.specialization,
                        myConsultationItem.myAppointment.scheduleDate.toNewFormat(
                            oldFormat = "yyyy-MM-dd",
                            newFormat = "EEEE, dd MMMM yyyy",
                            isLocale = true
                        )
                    )
                )
            }
        }
    }

    override fun bindViewModel(): OnGoingFragmentVM {
        return ViewModelProvider(this, viewModelFactory)[OnGoingFragmentVM::class.java]
    }

    companion object {
        private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
    }
}