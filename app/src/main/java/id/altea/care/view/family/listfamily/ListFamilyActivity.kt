package id.altea.care.view.family.listfamily

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.observe
import id.altea.care.databinding.ActivityListFamilyBinding
import id.altea.care.databinding.ItemListFamilyBinding
import id.altea.care.view.common.item.ItemGeneralErrorRetry
import id.altea.care.view.common.item.ItemLoadingLottie
import id.altea.care.view.common.item.ProgressBarItem
import id.altea.care.view.family.FamilyRouter
import id.altea.care.view.family.detailfamily.DetailFamilyActivity.Companion.RESULT_UPDATE_USER
import id.altea.care.view.family.item.*

class ListFamilyActivity : BaseActivityVM<ActivityListFamilyBinding, ListFamilyVM>() {

    private val itemAdapter by lazy { GenericItemAdapter() }
    private val fastAdapter by lazy { GenericFastItemAdapter() }
    private val router by lazy { FamilyRouter() }
    private lateinit var endlessScroll: EndlessRecyclerOnScrollListener

    override fun observeViewModel(viewModel: ListFamilyVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.patientListEvent, ::handleFamilyListEvent)
        observe(viewModel.isLoadingFirstEvent, ::handleLoadingFirstPage)
        observe(viewModel.isLoadingMoreEvent, ::handleLoadingMoreEvent)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.isNextPageAvailableEvent, ::handleNextPageAvailable)
        observe(viewModel.primaryFamilyEvent, { handlePrimaryEvent() })
    }

    override fun bindViewModel(): ListFamilyVM = ViewModelProvider(this, viewModelFactory)[ListFamilyVM::class.java]

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityListFamilyBinding = ActivityListFamilyBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.str_list_family)
            listFamilyRecyclerview.let {
                it.layoutManager = LinearLayoutManager(this@ListFamilyActivity)
                it.adapter = fastAdapter
            }
            fastAdapter.addAdapter(1, itemAdapter)

            endlessScroll = object : EndlessRecyclerOnScrollListener(itemAdapter) {
                override fun onLoadMore(currentPage: Int) {
                    endlessScroll.disable()
                    baseViewModel?.getFamilyMember(currentPage + 1)
                }
            }.apply { disable() }

            listFamilyRecyclerview.addOnScrollListener(endlessScroll)
        }
        baseViewModel?.getFamilyMember(1)
    }


    var createLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            baseViewModel?.getFamilyMember(1)
            showSuccessSnackbar(getString(R.string.str_success_add_family))
        } else if (it.resultCode == RESULT_UPDATE_USER) {
            baseViewModel?.getFamilyMember(1)
        }
    }

    override fun initUiListener() {

        fastAdapter.addEventHook(object : ClickEventHook<GenericItem>() {
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemListFamilyBinding) {
                    val bindingView = viewHolder.binding as ItemListFamilyBinding
                    return listOf(
                            bindingView.itemFamilyImgMore,
                            bindingView.root
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
                when (v.id) {
                    R.id.itemFamilyImgMore -> {
                        router.openBottomSheetOption(this@ListFamilyActivity,
                                (item as ItemListFamily).patient.id ?: "-1L", position)
                    }
                    else -> {
                        router.openDetailFamily(this@ListFamilyActivity, (item as ItemListFamily).patient, createLauncher)
                    }
                }


            }
        })


        viewBinding?.listFamilySwipe?.let {
            it.setOnRefreshListener {
                endlessScroll.resetPageCount(0)
                Handler(Looper.getMainLooper()).postDelayed({
                    it.isRefreshing = false
                }, 200)
            }
        }


    }

    private fun handlePrimaryEvent() {
        baseViewModel?.getFamilyMember(1)
        showSuccessSnackbar(getString(R.string.message_success_set_primary_key_family))
    }

    private fun handleFamilyListEvent(list: List<PatientFamily>?) {
        if (!list.isNullOrEmpty()) {
            val sortedList = list.sortedByDescending { it.isDefault }
            if (fastAdapter.itemCount > 0) {
                fastAdapter.remove(fastAdapter.itemCount - 1)
            }
            fastAdapter.add(ItemTitleFamily(getString(R.string.str_main_profile)))
            sortedList.map {
                if (it.isDefault == true) {
                    fastAdapter.add(ItemListFamily(it))
                }
            }
            fastAdapter.add(ItemTitleFamily(getString(R.string.str_other_profil)))
            if (sortedList.filter { it.isDefault == false }.isEmpty()) {
                fastAdapter.add(ItemFamilyEmpty())
            } else {
                sortedList.filter { it.isDefault == false }.map {
                    fastAdapter.add(ItemListFamily(it))
                }
            }
        }
        fastAdapter.add(ItemFamilyBtnAdd {
            router.openAddFamily(this, createLauncher)
        })
    }

    private fun handleLoadingFirstPage(showLoading: Boolean?) {
        fastAdapter.clear()
        if (showLoading == true) {
            fastAdapter.add(ItemLoadingLottie())
        }
        enableSwipeRefresh(showLoading == false)
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ServerError, Failure.NetworkConnection -> {
                if (fastAdapter.itemCount == 0) fastAdapter.add(ItemGeneralErrorRetry {
                    baseViewModel?.getFamilyMember(1)
                })
            }
            else -> super.handleFailure(failure)
        }
    }

    private fun handleLoadingMoreEvent(showLoading: Boolean?) {
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

    private fun enableSwipeRefresh(isEnable: Boolean) {
        viewBinding?.listFamilySwipe?.isEnabled = isEnable
    }

    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, ListFamilyActivity::class.java)
        }
    }
}