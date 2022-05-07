package id.altea.care.view.promotion.detail

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.PromotionDetail
import id.altea.care.core.ext.*
import id.altea.care.databinding.ActivityDetailPromotionBinding
import id.altea.care.R
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.view.common.item.AppointmentEmptyItem
import id.altea.care.view.home.failure.HomeFailure
import java.util.concurrent.TimeUnit


class PromotionDetailActivity : BaseActivityVM<ActivityDetailPromotionBinding,PromotionDetailVM>() {

    private val idPromotion  by lazy {
        intent.getIntExtra(EXTRA_DATA_ID_PROMOTION,0)
    }
    private var promotionDetailData : PromotionDetail? = null
    private var clipBoard  : ClipboardManager? = null

    override fun bindToolbar(): Toolbar? {
       return viewBinding?.appbar?.toolbar
    }

    override fun enableBackButton(): Boolean {
       return true
    }

    override fun getUiBinding(): ActivityDetailPromotionBinding {
       return ActivityDetailPromotionBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.appbar?.txtToolbarTitle?.text = getString(R.string.str_detail_promotion)
        clipBoard =  getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        baseViewModel?.getPromotionDetail(idPromotion)
    }

    override fun initUiListener() {
        viewBinding?.run {
            promotionDetailBtnMoreView.onSingleClick().subscribe {
                mDeepLinkManager.setDeepLinkUri(Uri.parse(promotionDetailData?.deeplinkUrlAndroid.toString()))
                mDeepLinkManager.executeDeepLink(this@PromotionDetailActivity)
            }.disposedBy(disposable)

            promotionDetailTextCopy.onSingleClick().subscribe {
                copyToClipboard(viewBinding?.promotionDetailTextCodeVoucher?.text.toString())
                showSuccessSnackbar(getString(R.string.str_success_copied_voucher),true)
            }.disposedBy(disposable)


            promotionDetailContentError.contentErrorBtnRetry.onSingleClick()
                .doOnNext { stateLoadingView() }
                .delay(800, TimeUnit.MILLISECONDS)
                .subscribe {
                    this@PromotionDetailActivity.runOnUiThread { baseViewModel?.getPromotionDetail(idPromotion) }
                }
                .disposedBy(disposable)

        }
    }

    private fun stateLoadingView(){
        viewBinding?.run {
            promotionDetailContentError.root.isVisible = false
            viewBinding?.promotionDetailScrollView?.isVisible=true
            viewBinding?.promotionDetailBtnMoreView?.isVisible = true
            detailPromotionTitle.isVisible= true
            appbar.root.isVisible = true
        }
    }

    override fun bindViewModel(): PromotionDetailVM {
        return ViewModelProvider(this, viewModelFactory)[PromotionDetailVM::class.java]
    }

    override fun observeViewModel(viewModel: PromotionDetailVM) {
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.promotionDetailEvent, ::handlePromotionDetail)
    }

    private fun handlePromotionDetail(promotionDetail: PromotionDetail?) {
        viewBinding?.run {
            promotionDetail?.let { promotionDetail ->
                promotionDetailData = promotionDetail
                promotionDetailImgPromotion.loadImage(promotionDetail.imageBannerDetail.orEmpty())
                detailPromotionTitle.text = promotionDetail.title
                detailPromotionDescriptionText.loadData( promotionDetail.description.orEmpty(),"text/html", "UTF-8")
                checkVoucherCodeExist(promotionDetail)
                checkDeeplinkExist(promotionDetail)

            }
        }

    }

    private fun checkVoucherCodeExist(promotionDetail: PromotionDetail) {
        if (promotionDetail.voucherCode?.isNotEmpty() == true && !promotionDetail.voucherCode.equals("-")) {
            viewBinding?.promotionDetailCodeText?.isVisible = true
            viewBinding?.promotionDetaillinearLayout?.isVisible = true
            viewBinding?.promotionDetailTextCodeVoucher?.text = promotionDetail.voucherCode
        }else{
            viewBinding?.promotionDetailCodeText?.isVisible = false
            viewBinding?.promotionDetaillinearLayout?.isVisible = false
        }
    }

    private fun checkDeeplinkExist(promotionDetail : PromotionDetail?) {
        when(promotionDetail?.deeplinkTypeAndroid){
            "DEEPLINK" ->{
                viewBinding?.promotionDetailBtnMoreView?.isVisible = promotionDetail.deeplinkUrlAndroid?.isNotEmpty() == true
            }
        }
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> stateErrorView()
            else -> super.handleFailure(failure)
        }
    }

    private fun stateErrorView(){
        viewBinding?.promotionDetailContentError?.root?.isVisible = true
        viewBinding?.promotionDetailBtnMoreView?.isVisible = false
        viewBinding?.promotionDetailScrollView?.isVisible=false
        viewBinding?.appbar?.root?.isVisible = false
    }

    companion object {
        const val EXTRA_DATA_ID_PROMOTION = "PromotionDetail.Id"
        fun createIntent(
            caller: Context,
            promotionId : Int
        ): Intent {
            return Intent(caller, PromotionDetailActivity::class.java).apply {
                putExtra(EXTRA_DATA_ID_PROMOTION,promotionId)
            }
        }
    }
}