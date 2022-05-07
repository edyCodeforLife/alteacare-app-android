package id.altea.care.view.promotion.teleconsultation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.PromotionList
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.delay
import id.altea.care.core.ext.observe
import id.altea.care.databinding.ActivityPromotionListDetailBinding
import id.altea.care.databinding.ItemAddressBinding
import id.altea.care.databinding.ItemPromotionDetailListBinding
import id.altea.care.view.common.item.ItemGeneralErrorRetry
import id.altea.care.view.common.item.ItemLoadingLottie
import id.altea.care.view.common.item.ProgressBarItem
import id.altea.care.view.generalsearch.SymptomListActivity
import id.altea.care.view.promotion.teleconsultation.item.ItemPromotionTeleconsultation
import id.altea.care.view.specialistsearch.SpecialistSearchActivity
import kotlinx.android.synthetic.main.item_promotion_banner.view.*
import kotlinx.android.synthetic.main.item_promotion_detail_list.view.*

class PromotionTeleconsultationActivity : BaseActivityVM<ActivityPromotionListDetailBinding,PromotionTeleconsultationVM>() {

    private val itemAdapter  by lazy { GenericItemAdapter() }
    private val fastAdapter by lazy { GenericFastItemAdapter() }
    private lateinit var endlessScroll : EndlessRecyclerOnScrollListener

    private val router = PromotionTeleconsultationRouter()

    private val promotionType by lazy {
        intent.getStringExtra(EXTRA_PROMOTION_TYPE)
    }

    override fun bindToolbar(): Toolbar? {
       return viewBinding?.appbar?.toolbar
    }

    override fun enableBackButton(): Boolean  = true

    override fun getUiBinding(): ActivityPromotionListDetailBinding {
       return ActivityPromotionListDetailBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        promotionType?.let { promotionType ->
            baseViewModel?.onFirstLaunch(promotionType)
        }
        initRecyclerView()
        baseViewModel?.getPromotionList(1)
    }

    override fun initUiListener() {
        viewBinding?.promotionListDetailSwpRfs?.let {
            it.setOnRefreshListener {
                fastAdapter.clear()
                endlessScroll.resetPageCount(0)
                disposable.delay(200){
                    it.isRefreshing = false
                }
            }
        }

        fastAdapter.addEventHook(object : ClickEventHook<GenericItem>(){
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemPromotionDetailListBinding) {
                    val bindingView = viewHolder.binding as ItemPromotionDetailListBinding
                    return listOf(
                        bindingView.itemBannerImgPromotionDetail
                    )
                }
                return super.onBindMany(viewHolder)
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<GenericItem>,
                item: GenericItem
            ) {
                when(v.id){
                    R.id.itemBannerImgPromotionDetail ->{
                        router.openDetailPromotion(this@PromotionTeleconsultationActivity,(item as ItemPromotionTeleconsultation).itemPromotion.promotionId)
                    }
                }
            }


        })
    }


    override fun bindViewModel(): PromotionTeleconsultationVM {
        return ViewModelProvider(this, viewModelFactory)[PromotionTeleconsultationVM::class.java]
    }

    override fun observeViewModel(viewModel: PromotionTeleconsultationVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, :: handleLoading)
        observe(viewModel.promotionListEvent, ::handlePromotionList)
        observe(viewModel.isNextPageAvailableEvent, ::handleNextPageAvailable)
        observe(viewModel.isLoadingMoreEvent, ::handleLoadingMore)
    }

    private fun handleLoadingMore(showLoading : Boolean?) {
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
        }
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ServerError, Failure.NetworkConnection -> {
                if (fastAdapter.itemCount == 0) fastAdapter.add(ItemGeneralErrorRetry {
                    baseViewModel?.getPromotionList(1)
                })
            }
            else -> super.handleFailure(failure)
        }
    }


    private fun handlePromotionList(promotionLists : List<PromotionList>?) {
        promotionLists?.map {
            viewBinding?.appbar?.txtToolbarTitle?.text = getString(R.string.str_promotion_teleconsultation)+it.promotionType
            fastAdapter.add(ItemPromotionTeleconsultation(it))
                endlessScroll.run {
                    enable()
                }
        }
    }

    private fun initRecyclerView(){
        viewBinding?.run {
            fastAdapter.addAdapter(1,itemAdapter)
            endlessScroll = object : EndlessRecyclerOnScrollListener(itemAdapter){
                override fun onLoadMore(currentPage: Int) {
                    endlessScroll.disable()
                    baseViewModel?.getPromotionList(currentPage+1)
                }
            }.apply { disable() }

            promotionGroupTitleRecycler.run {
                layoutManager = LinearLayoutManager(this@PromotionTeleconsultationActivity)
                adapter = fastAdapter
                addOnScrollListener(endlessScroll)
            }
        }
    }


    companion object {
        private const val EXTRA_PROMOTION_TYPE = "PromotionTeleconsultation.Type"

        fun createIntent(caller: Context, promotionType: String?): Intent {
            return Intent(caller, PromotionTeleconsultationActivity::class.java)
                .putExtra(EXTRA_PROMOTION_TYPE, promotionType)

        }
    }
}