package id.altea.care.view.cancelstatusorder

import android.content.Intent
import androidx.fragment.app.FragmentManager
import id.altea.care.core.helper.util.ConstantIndexMenu
import id.altea.care.view.common.bottomsheet.OptionCancelBottomSheet
import id.altea.care.view.endcall.EndCallActivity
import id.altea.care.view.main.MainActivity
import id.altea.care.view.transition.TransitionActivity
import id.altea.care.view.transition.bottomsheet.CancelCallBottomSheet

class CancelStatusRouter {
    fun openMainActivity(caller: CancelStatusActvity,viewPagerIndex : Int? = 0) {
        caller.startActivity(
            MainActivity.createIntent(caller, ConstantIndexMenu.INDEX_MENU_MY_CONSULTATION,viewPagerIndex).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        caller.finish()
    }
}