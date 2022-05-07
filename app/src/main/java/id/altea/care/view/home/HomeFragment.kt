package id.altea.care.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import id.altea.care.R
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.SpecialistCategoryPayloadBuilder
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.event.AppointmentStatusUpdateEvent
import id.altea.care.core.domain.event.HomeWidgetsEvent
import id.altea.care.core.domain.event.MainFragmentTabCreatedEvent
import id.altea.care.core.domain.model.*
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.*
import id.altea.care.core.helper.RxBus
import id.altea.care.core.helper.validator.TimeConversionValidator.Companion.istTimeConversion
import id.altea.care.databinding.FragmentHomeBinding
import id.altea.care.databinding.ItemPromotionBannerBinding
import id.altea.care.view.common.enums.TypeAppointment.*
import id.altea.care.view.common.item.AppointmentEmptyItem
import id.altea.care.view.common.item.BannerItem
import id.altea.care.view.common.item.ScheduleItem
import id.altea.care.view.common.item.SpecialistItem
import id.altea.care.view.common.ui.LinePagerIndicatorDecoration
import id.altea.care.view.consultation.PageType
import id.altea.care.view.home.failure.HomeFailure
import id.altea.care.view.home.item.HomeWidgetsItem
import id.altea.care.view.home.item.PromotionItem
import id.altea.care.view.specialist.model.SpecialistItemModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by trileksono on 12/03/21.
 */
class HomeFragment : BaseFragmentVM<FragmentHomeBinding, HomeFragmentVM>() {

    @Inject
    lateinit var analyticManager: AnalyticManager

    private val router by lazy { HomeRouter() }
    private val scheduleItem = ItemAdapter<IItem<*>>()
    private val scheduleAdapter = FastAdapter.with(scheduleItem)

    private val specialistItem = ItemAdapter<SpecialistItem>()
    private val specialistAdapter = FastAdapter.with(specialistItem)
    private var doctor: ConsultationDoctor? = null

    private val bannerItem = ItemAdapter<BannerItem>()
    private val bannerAdapter = FastAdapter.with(bannerItem)

    private val homeMenuItem = ItemAdapter<HomeWidgetsItem>()
    private val homeMenuAdapter = FastAdapter.with(homeMenuItem)

    private val scroll = 0

    private val promotionItem = ItemAdapter<IItem<*>>()
    private val promotionAdapter = FastAdapter.with(promotionItem)

    private var isOperationalHour: Boolean? = false

    override fun observeViewModel(viewModel: HomeFragmentVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isUserLoggedInEvent, { it?.let { handleView(it) } })
        observe(viewModel.specialistEvent, ::handleResponseSpecialist)
        observe(viewModel.profileEvent, ::handleResponseProfile)
        observe(viewModel.appointmentEvent, ::handleResponseAppointment)
        observe(viewModel.bannerEvent, ::handleResponseBanner)
        observe(viewModel.bannerClickEvent, ::handleBannerClickEvent)
        observe(viewModel.showButtonSwitchAccountEvent, ::handleButtonSwitchAccount)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.operationalHourMAEvent, ::handleOperationalMA)
        observe(viewModel.widgets, ::handleWidgets)
        observe(viewModel.promotionList, ::handlePromotion)
    }

    private fun handleWidgets(widgetsList: List<Widgets>?) {
        if (!widgetsList.isNullOrEmpty()) {
            viewBinding?.cardHomeMenu?.isVisible = true
            widgetsList.map { itemMenu ->
                homeMenuItem.add(HomeWidgetsItem(itemMenu))
            }
        } else {
            viewBinding?.cardHomeMenu?.isVisible = false
        }
    }

    private fun handleOperationalMA(dataOperationalHourMA: OperationalHourMA?) {
        dataOperationalHourMA.let { letOperationalMa ->
            isOperationalHour = istTimeConversion(
                letOperationalMa?.operationalHourStart.orEmpty(),
                letOperationalMa?.operationalHourEnd.orEmpty()
            )
        }
    }

    private fun handleButtonSwitchAccount(isVisible: Boolean?) {
        viewBinding?.homeProfileLayout?.homeImgSwitchAccount?.isVisible = isVisible == true
    }

    override fun getUiBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        viewModel?.onFirstLaunch()
        stateLoadingView()
        initRecyclerView()
        listenRxBus()
        setAutoScrollPromoPage()


        // send event when home fragment was initialize to main activity
        RxBus.post(MainFragmentTabCreatedEvent(isFragmentFinishCreated = true))
    }


    fun setAutoScrollPromoPage() {
        var count = 0
        Observable.interval(5000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                if (count == viewBinding?.homeRecyclerPromo?.adapter?.itemCount) {
                    count = 0
                } else if (count < viewBinding?.homeRecyclerPromo?.adapter?.itemCount ?: -1) {
                    viewBinding?.homeRecyclerPromo?.smoothScrollToPosition(count)
                    count++
                }
            }.subscribe().disposedBy(disposable)
    }


    private fun initRecyclerView() {
        viewBinding?.run {
            homeRecyclerSchedule.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = scheduleAdapter
                PagerSnapHelper().attachToRecyclerView(this)
            }

            homeRecyclerSpecialist.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = specialistAdapter
            }

            homeRecyclerBanner.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = bannerAdapter
                PagerSnapHelper().attachToRecyclerView(this)
                setPadding(0, 0, 50, 0)
                clipToPadding = false
            }

            rvHomeMenu.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = homeMenuAdapter
            }

            homeRecyclerSchedule.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    scheduleAdapter.notifyAdapterDataSetChanged()
                }
            })

            homeRecyclerPromo.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = promotionAdapter

            }

        }
    }

    private fun listenRxBus() {
        RxBus.getEvents().subscribe {
            when (it) {
                is AppointmentStatusUpdateEvent -> {
                    viewModel?.getMyAppointment()
                    requireActivity().runOnUiThread {
                        viewModel?.getOperationalHourMa()
                    }
                }
            }
        }.disposedBy(disposable)
    }

    override fun onReExecute() {}

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is NotFoundFailure.DataNotExist -> scheduleItem.add(AppointmentEmptyItem(R.string.empty_appointment))
            is HomeFailure.HomeSpecializationFailure -> stateErrorView()
            is HomeFailure.HomeBannerClickNeedLogin -> router.openLogin(requireContext())
            is NotFoundFailure.DataPromotionNotExist -> promotionItem.add(AppointmentEmptyItem(R.string.str_no_promotion_exist))
            is Failure.NetworkConnection -> stateErrorView()
            else -> super.handleFailure(failure)
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            homeEdtxSearch.onSingleClick()
                .subscribe { router.openGeneralSearch(requireContext()) }
                .disposedBy(disposable)

            homeBtnLogin.onSingleClick()
                .subscribe { router.openLogin(requireContext()) }
                .disposedBy(disposable)

            homeImgNotification.onSingleClick()
                .subscribe { router.openNotification(requireContext()) }
                .disposedBy(disposable)

            homeEdtxSearch.onSingleClick()
                .subscribe { router.openGeneralSearch(requireContext()) }
                .disposedBy(disposable)

            homeContentError.contentErrorBtnRetry.onSingleClick()
                .doOnNext { stateLoadingView() }
                .delay(800, TimeUnit.MILLISECONDS)
                .subscribe {
                    requireActivity().runOnUiThread { viewModel?.onFirstLaunch() }
                }
                .disposedBy(disposable)

            homeProfileLayout.homeImgSwitchAccount.onSingleClick()
                .subscribe {
                    router.openSwitchAccount(
                        childFragmentManager,
                        viewModel?.accountLoggedIn ?: emptyList(),
                        // when user selected account bottom sheet
                        { selectAccountCallback -> viewModel?.onSwitchAccount(selectAccountCallback.userId) },
                        addAccountCallback = {   // when user select to add account
                            router.openBottomSheetCreateOrAddAccount(
                                fragmentManager = childFragmentManager,
                                addAccountCallback = { router.openLogin(requireContext()) },
                                createAccountCallback = { router.openRegistration(requireContext()) }
                            )
                        })
                }
                .disposedBy(disposable)

            relLiveChat.onSingleClick()
                .subscribe {
                    router.openLiveChatBottomSheet(
                        childFragmentManager,
                        profile = viewModel?.profileEvent?.value
                    )
                }.disposedBy(disposable)

            txtShowMorePromo.onSingleClick()
                .subscribe {
                    router.openPromotionGroupActivity(requireActivity())
                }.disposedBy(disposable)

        }

        scheduleAdapter.onClickListener = { _, _, item, _ ->
            if (item is ScheduleItem) onClickAppointmentSchedule(item.myAppointment)
            true
        }

        specialistAdapter.onClickListener = { _, _, item, _ ->
            router.openSpecialistSearch(
                requireContext(),
                item.specialist.specializationId ?: "",
            )
            sendEventSpecialistCategoryToAnalytics(
                item.specialist.specializationId,
                item.specialist.name
            )
            true
        }

        bannerAdapter.onClickListener = { _, _, item, _ ->
            item.itemImgBanner?.let { viewModel?.onBannerClicked(it) }
            true
        }

        homeMenuAdapter.onClickListener = { _, _, item, _ ->
            when {
                !item.itemWidgetsHome.needLogin -> {
                    listenerNotNeedLogin(item.itemWidgetsHome)
                }

                item.itemWidgetsHome.needLogin && viewModel?.isUserLoggedInEvent?.value == false -> {
                    router.openLogin(requireContext())
                }

                item.itemWidgetsHome.needLogin && viewModel?.isUserLoggedInEvent?.value == true -> {
                    listenerNeedLogin(item.itemWidgetsHome)
                }
            }
            true
        }



        promotionAdapter.addEventHook(object : ClickEventHook<IItem<*>>() {

            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemPromotionBannerBinding) {
                    val bindingView = viewHolder.binding as ItemPromotionBannerBinding
                    return listOf(
                        bindingView.itemBannerImgPromotion
                    )
                }
                return super.onBindMany(viewHolder)
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<IItem<*>>,
                item: IItem<*>
            ) {
                when (v.id) {
                    R.id.itemBannerImgPromotion -> {
                        router.openDetailPromotion(
                            requireActivity(),
                            (item as PromotionItem).item.promotionId
                        )
                    }
                }

            }


        })


    }

    private fun listenerNeedLogin(itemWidgetsHome: Widgets) {
        when (itemWidgetsHome.deepLinkType) {
            "DEEPLINK" -> {
                RxBus.post(HomeWidgetsEvent(itemWidgetsHome.deeplinkUrl))
            }
            "WEB" -> {
                itemWidgetsHome.deeplinkUrl.let { url ->
                    viewModel?.replaceVaccineUrl(url)?.let { vaccineUrl ->
                        router.openWebView(
                            requireActivity(),
                            vaccineUrl,
                            viewModel?.setupCookie()
                        )
                    }
                }
            }
        }
    }

    private fun listenerNotNeedLogin(itemWidgetsHome: Widgets) {
        when (itemWidgetsHome.deepLinkType) {
            "DEEPLINK" -> {
                RxBus.post(HomeWidgetsEvent(itemWidgetsHome.deeplinkUrl))
            }
            "WEB" -> {
                itemWidgetsHome.deeplinkUrl.let { url ->
                    router.openWebView(
                        requireActivity(),
                        url
                    )
                }
            }
        }
    }

    private fun sendEventSpecialistCategoryToAnalytics(
        specialistCategoryId: String?,
        specialistCategoryName: String?
    ) {
        analyticManager.trackingEventSpecialistCategory(
            SpecialistCategoryPayloadBuilder(
                specialistCategoryId,
                specialistCategoryName
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun handleResponseProfile(profile: Profile?) {
        viewBinding?.run {
            homeProfileLayout.imgProfile.loadImage(
                profile?.userDetails?.avatar?.formats?.small.orEmpty(),
                R.drawable.ic_change_photo_profile,
                R.drawable.ic_logo_loading
            )
            homeProfileLayout.homeTxtProfileAge.text =
                String.format(
                    getString(R.string.s_age),
                    profile?.userDetails?.age?.year ?: "0",
                    profile?.userDetails?.age?.month ?: "0"
                )
            homeProfileLayout.homeTxtProfileName.text = "${profile?.firstName} ${profile?.lastName}"
            homeProfileLayout.homeTxtProfilePoin.isVisible = false
            homeTxtNotifBadge.text = ""
        }
    }

    private fun handleResponseAppointment(list: List<AppointmentData>?) {
        list?.let { appointmentData ->
            scheduleItem.clear()
            appointmentData.map {
                scheduleItem.add(
                    ScheduleItem(
                        AppointmentData.mapToMyAppointment(it),
                        isOperationalHour
                    )
                )
                scheduleAdapter.notifyAdapterDataSetChanged()
                doctor = ConsultationDoctor(
                    id = it.doctor?.doctorId,
                    name = it.doctor?.doctorName,
                    photo = it.doctor?.photo,
                    specialist = it.doctor?.specialization,
                    hospital = null
                )
            }

            if (appointmentData.size > 1) {
                viewBinding?.homeRecyclerSchedule?.run {
                    addItemDecoration(LinePagerIndicatorDecoration())
                }
            }
        }
    }

    private fun handleResponseSpecialist(specialists: List<Specialization>?) {
        stateSuccessView()
        specialists?.map {
            specialistItem.add(
                SpecialistItem(
                    SpecialistItemModel(it.icon?.url, it.name.orEmpty(), it.specializationId)
                )
            )
        }
    }

    private fun handleBannerClickEvent(url: String?) {
        url?.let { bannerUrl -> router.openWebView(requireActivity(), bannerUrl) }
    }

    private fun handleResponseBanner(banners: List<Banner>?) {
        if (!banners.isNullOrEmpty()) {
            bannerItem.clear()
            banners.map {
                viewBinding?.homeGroupBanner?.isVisible = true
                bannerItem.add(BannerItem(it))
            }
            if (banners.size > 1) {
                viewBinding?.homeRecyclerBanner?.run {
                    addItemDecoration(LinePagerIndicatorDecoration())
                }
            }
        } else {
            viewBinding?.homeGroupBanner?.isVisible = false
        }
    }

    private fun handleView(isLoggedIn: Boolean) {
        viewBinding?.run {
            homeGroupLogin.isVisible = isLoggedIn
            homeGroupUnLogin.isVisible = !isLoggedIn
        }
    }

    private fun onClickAppointmentSchedule(myAppointment: MyAppointment) {
        when (myAppointment.status) {
            WAITING_FOR_PAYMENT -> {
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
                    priceDoctorDecimal = (myAppointment.transaction?.total ?: 0).toLong(),
                    priceStrikeDecimal = (myAppointment.transaction?.total ?: 0).toLong(),
                    priceStrike = (myAppointment.transaction?.total ?: 0).toString(),
                    specializationId = "default"
                )

                if (myAppointment.transaction?.redirectUrl == null) {
                    router.openConsultation(
                        requireActivity(),
                        PageType.PAYMENT,
                        consultation,
                        myAppointment.orderCode,
                        myAppointment.id
                    )
                } else {
                    viewModel?.getRedirectUrl(myAppointment.transaction.redirectUrl)?.let {
                        router.openPaymentInformation(
                            requireActivity() as AppCompatActivity,
                            myAppointment.id,
                            it,
                            myAppointment.orderCode
                        )
                    }
                }
            }

            MEET_SPECIALIST, PAID, COMPLETED, WAITING_FOR_MEDICAL_RESUME -> {
                myAppointment.status.let { typeAppointment ->
                    router.openConsultationDetail(
                        requireActivity(),
                        typeAppointment,
                        myAppointment.id
                    )
                }
            }

            UNVERIFIED, NEW, PROCESS_GP -> {
                handleOperationalHourMaListener(
                    myAppointment,
                    viewModel?.operationalHourMAEvent?.value
                )
            }

            ON_GOING -> {
                router.openCallWithSpecialist(
                    requireActivity() as AppCompatActivity,
                    myAppointment.id, doctor,
                    InfoDetail(
                        myAppointment.doctorName, myAppointment.specialization,
                        myAppointment.scheduleDate.toNewFormat(
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

    override fun bindViewModel(): HomeFragmentVM {
        return ViewModelProvider(this, viewModelFactory).get(HomeFragmentVM::class.java)
    }

    override fun initMenu(): Int = 0

    private fun stateErrorView() {
        viewBinding?.run {
            homeGroupBanner.isVisible = false
            homePLoading.isVisible = false
            homeConstraint.isVisible = false
            homeContentError.root.isVisible = true
            homeGroupPromo.isVisible = false
        }
    }

    private fun stateLoadingView() {
        viewBinding?.run {
            homePLoading.isVisible = true
            homeConstraint.isVisible = false
            homeContentError.root.isVisible = false
        }
    }

    private fun stateSuccessView() {
        viewBinding?.run {
            homePLoading.isVisible = false
            homeConstraint.isVisible = true
            homeContentError.root.isVisible = false
        }
    }

    private fun handleOperationalHourMaListener(
        myAppointment: MyAppointment,
        operationalHourMA: OperationalHourMA?
    ) {
        operationalHourMA?.let { dataOperationalHour ->
            if (!istTimeConversion(
                    dataOperationalHour.operationalHourStart.orEmpty(),
                    dataOperationalHour.operationalHourEnd.orEmpty()
                )
            ) {
                router.openMaOperationalHourBottomSheet(
                    activity?.supportFragmentManager,
                    dataOperationalHour.operationalHour.orEmpty()
                )
            } else {
                router.openReconsultationWithMA(
                    requireActivity() as AppCompatActivity,
                    myAppointment.id,
                    myAppointment.orderCode,
                    UserProfile(myAppointment.patient?.name),
                    InfoDetail(
                        myAppointment.doctorName, myAppointment.specialization,
                        myAppointment.scheduleDate.toNewFormat(
                            oldFormat = "yyyy-MM-dd",
                            newFormat = "EEEE, dd MMMM yyyy",
                            isLocale = true
                        )
                    )
                )
            }
        }
    }

    private fun handlePromotion(promotionList: List<PromotionList>?) {
        viewBinding?.run {
            if (!promotionList.isNullOrEmpty()) {
                viewBinding?.homeGroupPromo?.isVisible = true
                promotionItem.clear()
                promotionList.map {
                    promotionItem.add(PromotionItem(it))
                }

                if (promotionList.size > 1) {
                    val pagerSnapHelper = PagerSnapHelper()
                    pagerSnapHelper.attachToRecyclerView(homeRecyclerPromo)
                    indicator.attachToRecyclerView(homeRecyclerPromo, pagerSnapHelper)
                    promotionAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver());

                }
            } else {
                viewBinding?.homeGroupPromo?.isVisible = false
            }
        }
    }

}
