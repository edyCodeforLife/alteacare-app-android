package id.altea.care.view.promotion.group

import android.app.Activity
import id.altea.care.core.domain.model.PromotionDetail
import id.altea.care.view.promotion.detail.PromotionDetailActivity
import id.altea.care.view.promotion.teleconsultation.PromotionTeleconsultationActivity

class PromotionGroupRouter {

    fun openTeleconsultationActivity(source : Activity,promotionType : String){
        source.startActivity(PromotionTeleconsultationActivity.createIntent(source,promotionType))
    }

    fun openDetailPromotion(source: Activity,promotionId : Int){
        source.startActivity(PromotionDetailActivity.createIntent(source,promotionId))
    }
}