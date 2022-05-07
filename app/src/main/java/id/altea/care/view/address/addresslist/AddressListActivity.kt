package id.altea.care.view.address.addresslist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.*
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
import id.altea.care.core.domain.model.UserAddress
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.observe
import id.altea.care.databinding.ActivityListAddressBinding
import id.altea.care.databinding.ItemAddressBinding
import id.altea.care.view.address.addresslist.item.ItemAddress
import id.altea.care.view.address.addresslist.item.ItemAddressBtnAdd
import id.altea.care.view.address.addresslist.item.ItemAddressEmpty
import id.altea.care.view.address.failure.AddressFailure
import id.altea.care.view.common.item.ItemGeneralErrorRetry
import id.altea.care.view.common.item.ItemLoadingLottie
import id.altea.care.view.common.item.ProgressBarItem
import kotlinx.android.synthetic.main.activity_list_address.*

class AddressListActivity : BaseActivityVM<ActivityListAddressBinding, AddressListVM>() {

    private val itemAdapter by lazy { GenericItemAdapter() }
    private val fastAdapter by lazy { GenericFastItemAdapter() }
    private val router by lazy { AddressListRouter() }
    private lateinit var endlessScroll: EndlessRecyclerOnScrollListener

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityListAddressBinding {
        return ActivityListAddressBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.toolbar_list_address)

            listAddressRecycler.let {
                it.layoutManager = LinearLayoutManager(this@AddressListActivity)
                it.adapter = fastAdapter
            }
            fastAdapter.addAdapter(1, itemAdapter)

            endlessScroll = object : EndlessRecyclerOnScrollListener(itemAdapter) {
                override fun onLoadMore(currentPage: Int) {
                    endlessScroll.disable()
                    baseViewModel?.getListAddress(currentPage + 1)
                }
            }.apply { disable() }

            listAddressRecycler.addOnScrollListener(endlessScroll)
        }
        baseViewModel?.getListAddress(1)
    }

    override fun initUiListener() {
        fastAdapter.addEventHook(object : ClickEventHook<GenericItem>() {
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemAddressBinding) {
                    val bindingView = viewHolder.binding as ItemAddressBinding
                    return listOf(
                        bindingView.itemAddressImgMore,
                        bindingView.itemAddressTextChange,
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
                    R.id.itemAddressImgMore -> {
                        router.openBottomSheetOption(
                            this@AddressListActivity,
                            (item as ItemAddress).address.id ?: "-1L",
                            position
                        )
                    }
                    R.id.itemAddressTextChange -> {
                        router.openEditAddressForm(
                            this@AddressListActivity,
                            createOrUpdateLauncher,
                            (item as ItemAddress).address
                        )
                    }
                    else -> {
                        intent.getIntExtra(EXTRA_REQUEST_CODE, -1).let { requestCode ->
                            if (requestCode != -1) {
                                setResult(
                                    requestCode,
                                    intent.putExtra("address", (item as ItemAddress).address)
                                )
                                finish()
                            }
                        }
                    }
                }


            }
        })

        viewBinding?.listAddressSwipe?.let {
            it.setOnRefreshListener {
                endlessScroll.resetPageCount(0)
                Handler(Looper.getMainLooper()).postDelayed({
                    it.isRefreshing = false
                }, 200)
            }
        }
    }


    override fun observeViewModel(viewModel: AddressListVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.addressListEvent, ::handleAddressListEvent)
        observe(viewModel.isLoadingFirstEvent, ::handleLoadingFirstPage)
        observe(viewModel.isLoadingMoreEvent, ::handleLoadingMoreEvent)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.isNextPageAvailableEvent, ::handleNextPageAvailable)
        observe(viewModel.setPrimaryAddressEvent, ::handleSetPrimaryAddressEvent)
        observe(viewModel.deleteAddressEvent, { handleDeleteAddressEvent() })
    }

    override fun bindViewModel(): AddressListVM {
        return ViewModelProvider(this, viewModelFactory)[AddressListVM::class.java]
    }

    private fun handleAddressListEvent(list: List<UserAddress>?) {
        if (!list.isNullOrEmpty()) {
            if (fastAdapter.itemCount > 0) {
                fastAdapter.remove(fastAdapter.itemCount - 1)
            }
            list.map { fastAdapter.add(ItemAddress(it)) }
        } else if (fastAdapter.itemCount == 0) {
            fastAdapter.add(ItemAddressEmpty())
        }

        fastAdapter.add(ItemAddressBtnAdd {
            router.openCreateAddressForm(
                this,
                createOrUpdateLauncher
            )
        })
    }

    private fun handleLoadingFirstPage(showLoading: Boolean?) {
        fastAdapter.clear()
        if (showLoading == true) {
            fastAdapter.add(ItemLoadingLottie())
        }
        enableSwipeRefresh(showLoading == false)
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

    private fun handleSetPrimaryAddressEvent(position: Int?) {
        position?.let {
            fastAdapter.adapterItems.mapIndexed { index, iItem ->
                if (iItem is ItemAddress && iItem.isPrimary()) {
                    fastAdapter[index] = iItem.apply { unsetPrimary() }
                    fastAdapter.notifyItemChanged(index)
                }
            }
            fastAdapter.adapterItems[it].apply { (this as ItemAddress).setPrimary() }
            fastAdapter.notifyItemChanged(position)
            showSuccessSnackbar(getString(R.string.message_success_set_primary_key))
        }
    }

    private fun handleDeleteAddressEvent() {
        baseViewModel?.getListAddress(1)
    }

    var createOrUpdateLauncher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            baseViewModel?.getListAddress(1)
        }
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is AddressFailure.SetPrimaryAddressFailure, AddressFailure.DeleteAddressFailure -> {
                showErrorSnackbar(getString(R.string.general_error_item_retry))
            }
            is Failure.ServerError, Failure.NetworkConnection -> {
                if (fastAdapter.itemCount == 0) fastAdapter.add(ItemGeneralErrorRetry {
                    baseViewModel?.getListAddress(1)
                })
            }
            else -> super.handleFailure(failure)
        }
    }

    private fun enableSwipeRefresh(isEnable: Boolean) {
        viewBinding?.listAddressSwipe?.isEnabled = isEnable
    }

    companion object {
        private const val EXTRA_REQUEST_CODE = "AddressListActivity.requestCode"
        fun createIntent(caller: Context, requestCode: Int? = null): Intent {
            return Intent(caller, AddressListActivity::class.java)
                .putExtra(EXTRA_REQUEST_CODE, requestCode)
        }
    }

}
