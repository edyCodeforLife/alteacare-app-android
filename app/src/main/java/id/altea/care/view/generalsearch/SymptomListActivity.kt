package id.altea.care.view.generalsearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Symptoms
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.delay
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.isHtmlText
import id.altea.care.databinding.ActivitySymptomsListBinding
import id.altea.care.view.common.item.ItemGeneralErrorRetry
import id.altea.care.view.common.item.ProgressBarItem
import id.altea.care.view.generalsearch.item.SymptomItem

class SymptomListActivity : BaseActivityVM<ActivitySymptomsListBinding, GeneralSearchVM>() {

    private val keyword by lazy { intent?.getStringExtra(EXTRA_KEYWORD) }
    private val router by lazy { SymptomListRouter() }
    private val itemAdapter by lazy { GenericItemAdapter() }
    private val fastAdapter by lazy { GenericFastItemAdapter() }

    private lateinit var endlessScroll: EndlessRecyclerOnScrollListener

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.toolbar

    override fun getUiBinding(): ActivitySymptomsListBinding {
        return ActivitySymptomsListBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        disposable.delay(800){
            fastAdapter.addAdapter(1,itemAdapter)
        }
        endlessScroll = object : EndlessRecyclerOnScrollListener(itemAdapter) {
            override fun onLoadMore(currentPage: Int) {
                handleLoadingMoreEvent(true)
                baseViewModel?.getSymptomsList(keyword.orEmpty(), currentPage + PAGE)
            }
        }.apply { disable() }

        viewBinding?.run {
            rvSymptom.apply {
                layoutManager = LinearLayoutManager(this@SymptomListActivity)
                adapter = fastAdapter
                addOnScrollListener(endlessScroll)
            }
        }
        baseViewModel?.getSymptomsList(keyword.orEmpty(), PAGE)
    }

    override fun initUiListener() {
        fastAdapter.onClickListener = { _, _, item, _ ->
            if (item is SymptomItem) {
                baseViewModel?.sendEventSearchResultToAnalytics(
                    specialistCategory = null,
                    specialistDoctorName = null,
                    symptom = item.data.name
                )
                router.openSpecialist(source = this, id = null, query = item.data.name ?: keyword)
            }
            false
        }

        viewBinding?.symptomSwipeRefresh?.let { swipe ->
            swipe.setOnRefreshListener {
                disposable.delay(1000) {
                    fastAdapter.clear()
                    endlessScroll.resetPageCount(0)
                    swipe.isRefreshing = false
                }
            }
        }
    }

    private fun handleLoadingMoreEvent(showLoading: Boolean?) {
        itemAdapter.clear()
        if (showLoading == true) {
            itemAdapter.add(ProgressBarItem())
        } else {
            itemAdapter.clear()
        }
    }

    private fun handleNextPageAvailable(isNextPageAvailable: Boolean?) {
        if (isNextPageAvailable == true) {
            endlessScroll.enable()
        } else {
            endlessScroll.disable()
            handleLoadingMoreEvent(false)
        }
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ServerError, Failure.NetworkConnection -> {
                if (fastAdapter.itemCount == 0) fastAdapter.add(ItemGeneralErrorRetry {
                    baseViewModel?.getSymptomsList(keyword.orEmpty(), PAGE)
                })
            }
            else -> super.handleFailure(failure)
        }
    }

    override fun observeViewModel(viewModel: GeneralSearchVM) {
        observe(viewModel.getSymptomsEvent, ::handleGetSymptoms)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.isNextPageAvailableEvent, ::handleNextPageAvailable)
        observe(viewModel.failureLiveData, ::handleFailure)
    }

    private fun handleGetSymptoms(symptomsList: List<Symptoms>?) {
        viewBinding?.run {
            if (!symptomsList.isNullOrEmpty()) {
                symptomsList.map { symptomsItem ->
                    fastAdapter.add(
                        SymptomItem(
                            Symptoms(
                                name = symptomsItem.name,
                                symptomId = symptomsItem.symptomId,
                                slug = symptomsItem.slug
                            )
                        )
                    )
                }
                txtInfoData.text = String.format(
                    getString(R.string.str_info_data_show_from_one),
                    fastAdapter.adapterItemCount,
                    getString(R.string.recomendation_symptom_for),
                    keyword
                ).isHtmlText()
                endlessScroll.enable()
            }
        }
    }

    override fun bindViewModel(): GeneralSearchVM {
        return ViewModelProvider(this, viewModelFactory)[GeneralSearchVM::class.java]
    }

    companion object {
        private const val EXTRA_KEYWORD = "KEYWORD"
        private const val PAGE = 1
        fun createIntent(
            caller: Context,
            keyword: String
        ): Intent {
            return Intent(caller, SymptomListActivity::class.java)
                .putExtra(EXTRA_KEYWORD, keyword)
        }
    }
}