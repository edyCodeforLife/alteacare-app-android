package id.altea.care.view.promotion.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.PromotionListGroup
import id.altea.care.core.ext.delay
import id.altea.care.core.ext.observe
import id.altea.care.databinding.ActivityPromotionGroupBinding
import id.altea.care.view.promotion.group.item.ItemTypePromotion
import kotlinx.android.synthetic.main.item_promotion.*
import kotlinx.android.synthetic.main.item_promotion.view.*
import kotlinx.android.synthetic.main.item_promotion_banner.view.*
import kotlinx.android.synthetic.main.item_single_simple_selected.view.*

class PromotionGroupActivity : BaseActivityVM<ActivityPromotionGroupBinding, PromotionVM>() {

    private val itemAdapter = ItemAdapter<ItemTypePromotion>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    val  router = PromotionGroupRouter()

    override fun bindToolbar(): Toolbar? {
        return viewBinding?.appbar?.toolbar
    }

    override fun enableBackButton(): Boolean {
        return true
    }

    override fun getUiBinding(): ActivityPromotionGroupBinding {
        return ActivityPromotionGroupBinding.inflate(layoutInflater)
    }

    override fun bindViewModel(): PromotionVM {
        return ViewModelProvider(this, viewModelFactory)[PromotionVM::class.java]
    }

    override fun observeViewModel(viewModel: PromotionVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.promotionListGroupEvent, ::handlePromotionListGroup)
    }

    private fun handlePromotionListGroup(listPromotionGroups: List<PromotionListGroup>?) {
        itemAdapter.clear()
        listPromotionGroups?.let { promotionsGroups ->
            promotionsGroups.mapIndexed { index ,promotionGroup ->
                itemAdapter.add(
                    ItemTypePromotion(
                        index,
                        promotionGroup.promotionTitle,
                        promotionGroup.promotionId,
                        promotionGroup.promotionList
                    )
                )
            }
        }

        //set listener when click banner item
        for( i in 0..fastAdapter.itemCount){
            fastAdapter.getItem(i)?.setListenerText(object  : ItemTypePromotion.onClickListener{
                override fun textChangeListener(idPromotion : Int) {
                    router.openDetailPromotion(this@PromotionGroupActivity,idPromotion)
                }

            })

        }

    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.appbar?.txtToolbarTitle?.text = getString(R.string.str_promotion)
        baseViewModel?.getPromotionListGroup()
        initRecycleView()

        viewBinding?.promotionGroupSwpRfs?.let {
            it.setOnRefreshListener {
                baseViewModel?.getPromotionListGroup()
                Handler(Looper.getMainLooper()).postDelayed({
                    it.isRefreshing = false
                }, 200)
            }
        }

    }

    private fun initRecycleView() {
        viewBinding?.run {
            promotionGroupTitleRecycler.apply {
                layoutManager = LinearLayoutManager(this@PromotionGroupActivity)
                adapter = fastAdapter
            }

        }
    }

    override fun initUiListener() {
        fastAdapter.addEventHook(object : ClickEventHook<ItemTypePromotion>(){
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                return viewHolder.itemView.txtShowMorePromoGroup
            }
                override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<ItemTypePromotion>,
                item: ItemTypePromotion
            ) {
                when(v.id){
                    R.id.txtShowMorePromoGroup ->{
                     router.openTeleconsultationActivity(this@PromotionGroupActivity,item.promotionTypeId)
                    }
                }
            }

        })

    }

    companion object {
        fun createIntent(
            caller: Context,
        ): Intent {
            return Intent(caller, PromotionGroupActivity::class.java)
        }
    }


}