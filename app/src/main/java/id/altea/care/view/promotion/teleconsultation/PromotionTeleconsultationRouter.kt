package id.altea.care.view.promotion.teleconsultation

import android.app.Activity
import id.altea.care.view.promotion.detail.PromotionDetailActivity

class PromotionTeleconsultationRouter {
    fun openDetailPromotion(source: Activity, promotionId : Int){
        source.startActivity(PromotionDetailActivity.createIntent(source,promotionId))
    }
}