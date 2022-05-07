package id.altea.care.view.myconsultation.history

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.widget.textChanges
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.event.MyConsultationFilterEvent
import id.altea.care.core.domain.model.AppointmentData
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.helper.RxBus
import id.altea.care.databinding.FragmentHistoryConsultationBinding
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.common.item.AppointmentEmptyItem
import id.altea.care.view.common.item.ProgressBarItem
import id.altea.care.view.myconsultation.item.MyConsultationItem
import java.util.concurrent.TimeUnit

abstract class BaseHistoryFragment<VM : BaseHistoryConsultationVM> :
    BaseFragmentVM<FragmentHistoryConsultationBinding, VM>() {

    protected lateinit var fastAdapter: GenericFastItemAdapter
    protected lateinit var footerAdapter: GenericItemAdapter
    protected lateinit var endlessScroll: EndlessRecyclerOnScrollListener

    override fun getUiBinding(): FragmentHistoryConsultationBinding {
        return FragmentHistoryConsultationBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        initRecyclerView()
        listenRxBus()
        viewModel?.onFirstLaunch()
    }

    abstract fun setStringResCardEmptyAppointment(): Int

    private fun initRecyclerView() {
        fastAdapter = FastItemAdapter()
        footerAdapter = ItemAdapter.items()
        fastAdapter.addAdapter(1, footerAdapter)

        endlessScroll = object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                viewBinding?.onGoingRecycler?.post { footerAdapter.clear() }
                viewModel?.loadMoreData(currentPage + 1)
            }
        }.apply { disable() }

        viewBinding?.run {
            onGoingRecycler.let {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = fastAdapter
                it.addOnScrollListener(endlessScroll)
            }
        }

        fastAdapter.onClickListener = { _, _, item, _ ->
            if (item is MyConsultationItem) {
                when (item.myAppointment.status) {
                    TypeAppointment.CANCELED_BY_GP,
                    TypeAppointment.CANCELED_BY_SYSTEM,
                    TypeAppointment.CANCELED_BY_USER,
                    TypeAppointment.PAYMENT_EXPIRED,
                    TypeAppointment.REFUNDED,
                    TypeAppointment.REFUND -> {
                        BaseHistoryConsultationRouter.openCancelDetail(
                            requireActivity(),
                            item.myAppointment.id
                        )
                    }
                    else -> item.myAppointment.status?.let {
                        BaseHistoryConsultationRouter.openConsultationDetail(
                            requireActivity(),
                            it,
                            item.myAppointment.id
                        )
                    }
                }
            }
            false
        }
    }

    abstract fun listenRxBus()

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is NotFoundFailure.DataNotExist -> {
                fastAdapter.clear()
                if (fastAdapter.adapterItemCount == 0)
                    fastAdapter.add(AppointmentEmptyItem(setStringResCardEmptyAppointment()))
            }
            is NotFoundFailure.EmptyListLoadMore -> endlessScroll.disable()
            else -> super.handleFailure(failure)
        }
    }

    override fun onReExecute() {}

    override fun initUiListener() {
        viewBinding?.run {

            historyImgSort.onSingleClick()
                .subscribe {
                    BaseHistoryConsultationRouter.openSortAppointmentBottomSheet(
                        this@BaseHistoryFragment,
                        viewModel?.sortType
                    ) {
                        viewModel?.changeSortType(it)
                    }
                }
                .disposedBy(disposable)

            historyImgFilter.onSingleClick()
                .subscribe {
                    RxBus.post(MyConsultationFilterEvent.OpenFilterEvent(viewModel?.patientFamily))
                }
                .disposedBy(disposable)

            onGoingSwipe.setOnRefreshListener { viewModel?.onFirstLaunch() }

            specialSearchEdtxSearch.textChanges()
                .skipInitialValue()
                .debounce(800, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (it.isEmpty()) {
                        viewModel?.updateTextSearch(null)
                    } else {
                        viewModel?.updateTextSearch(it.toString())
                    }
                }
                .disposedBy(disposable)
        }
    }

    override fun initMenu(): Int = 0

    override fun observeViewModel(viewModel: VM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoadingLivedata)
        observe(viewModel.isLoadingLoadMore, ::handleLoadingLoadMore)
        observe(viewModel.appointmentListEvent, ::handleResponseAppointment)
        observe(viewModel.appointmentListLoadMoreEvent, ::handleResponseLoadMoreAppointment)

    }

    private fun handleLoadingLivedata(showLoading: Boolean?) {
        viewBinding?.run { onGoingSwipe.isRefreshing = showLoading == true }
    }

    private fun handleLoadingLoadMore(showLoading: Boolean?) {
        if (showLoading == true && fastAdapter.itemCount > 0) {
            footerAdapter.add(ProgressBarItem())
        } else {
            footerAdapter.clear()
        }
    }

    private fun handleResponseLoadMoreAppointment(list: List<AppointmentData>?) {
        list?.let {
            if (it.isEmpty()) endlessScroll.disable()
            fastAdapter.add(list.map {
                MyConsultationItem(AppointmentData.mapToMyAppointment(it))
            })
        }
    }

    private fun handleResponseAppointment(list: List<AppointmentData>?) {
        list?.let {
            if (fastAdapter.itemCount > 0) {
                endlessScroll.resetPageCount(1)
                fastAdapter.clear()
            }

            fastAdapter.add(list.map {
                MyConsultationItem(AppointmentData.mapToMyAppointment(it))
            })
            endlessScroll.enable()
        }
    }

}
