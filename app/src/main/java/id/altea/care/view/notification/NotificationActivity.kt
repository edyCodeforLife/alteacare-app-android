package id.altea.care.view.notification

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.databinding.ActivityNotificationBinding
import id.altea.care.view.notification.item.NotificationDummy
import id.altea.care.view.notification.item.NotificationItem
import kotlinx.android.synthetic.main.fragment_specialist.view.*
import kotlinx.android.synthetic.main.toolbar_default_center.view.*

class NotificationActivity : BaseActivityVM<ActivityNotificationBinding, NotificationActivityVM>() {

    private val itemAdapter by lazy { ItemAdapter<NotificationItem>() }
    private val fastAdapter by lazy { FastAdapter.with(itemAdapter) }

    override fun observeViewModel(viewModel: NotificationActivityVM) {
    }

    override fun bindViewModel(): NotificationActivityVM {
        return ViewModelProvider(this, viewModelFactory).get(NotificationActivityVM::class.java)
    }

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityNotificationBinding {
        return ActivityNotificationBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.notification)
            notificationRecycler.let {
                it.layoutManager = LinearLayoutManager(this@NotificationActivity)
                it.adapter = fastAdapter
                it.addItemDecoration(
                    DividerItemDecoration(this@NotificationActivity, VERTICAL)
                )
            }
        }

        for (i in 0 until 10) {
            itemAdapter.add(
                NotificationItem(
                    NotificationDummy(
                        "Notifikasi $i",
                        "No. konsultasi 66870080$i.  akan segera dimulai, pastikan handphone dan internet kamu aktif, klik di sini untuk memulai konsultasi. ",
                        "blob:https://www.figma.com/e6c46911-bd4a-4c97-921e-1febe597c137",
                        "Hari ini 09:00",
                        i % 2 == 0
                    )
                )
            )
        }
    }

    override fun initUiListener() {
        fastAdapter.onClickListener = { _, _, item, pos ->
            if (!item.notifDummy.isRead) {
                fastAdapter.notifyAdapterItemChanged(pos, item.apply {
                    this.notifDummy.isRead = true
                })

            }
            false
        }
    }

}
