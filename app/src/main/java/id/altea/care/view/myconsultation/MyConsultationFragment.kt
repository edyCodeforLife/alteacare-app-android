package id.altea.care.view.myconsultation

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import id.altea.care.R
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.event.MyConsultationFilterEvent
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.helper.RxBus
import id.altea.care.databinding.FragmentMyConsultationBinding
import id.altea.care.view.consultation.ConsultationActivity
import id.altea.care.view.consultation.ConsultationFragment
import id.altea.care.view.consultation.ConsultationRouter
import id.altea.care.view.main.MainActivity
import id.altea.care.view.myconsultation.history.cancelfragment.MyConsultationCancelFragment
import id.altea.care.view.myconsultation.history.historyfragment.MyConsultationHistoryFragment
import id.altea.care.view.myconsultation.ongoing.OnGoingFragment


/**
 * Created by trileksono on 12/03/21.
 */
class MyConsultationFragment() :
    BaseFragmentVM<FragmentMyConsultationBinding, MyConsultationFragmentVM>() {

    val bundle = Bundle()

    override fun observeViewModel(viewModel: MyConsultationFragmentVM) {
        observe(viewModel.listPatientEvent, ::handleListPatientEvent)
    }

    private fun handleListPatientEvent(data: Pair<PatientFamily?, List<PatientFamily>>?) {
        data?.let {
            MyConsultationRouter.openFilterConsultation(
                this,
                it.second,
                it.first,
                callbackPatient = { RxBus.post(MyConsultationFilterEvent.SelectedFilterEvent(it)) }
            )
        }
    }

    override fun getUiBinding(): FragmentMyConsultationBinding {
        return FragmentMyConsultationBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        initViewPager()
        listenRxBus()
        val viewPagerIndex =  (activity as MainActivity).viewPagerIndex ?: 0

        viewBinding?.myConsulViewPager?.setCurrentItem(viewPagerIndex,true)
    }

    private fun listenRxBus() {
        RxBus.getEvents()
            .filter { isAdded }
            .subscribe {
                when (it) {
                    is MyConsultationFilterEvent.OpenFilterEvent -> {
                        viewModel?.onFilterClicked(it.patientFamily)
                    }
                }
            }.disposedBy(disposable)
    }

    private fun initViewPager() {
        viewBinding?.run {
            myConsulViewPager.adapter = MyConsultationPagerAdapter(
                listOf(
                    OnGoingFragment(),
                    MyConsultationHistoryFragment(),
                    MyConsultationCancelFragment()
//                    HistoryConversationFragment()
                ),
                childFragmentManager,
                lifecycle
            )
            TabLayoutMediator(myConsulTabLayout, myConsulViewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.ongoing)
                    1 -> tab.text = getString(R.string.history)
                    2 -> tab.text = getString(R.string.canceled)
//                    3 -> tab.text = "Percakapan"
                }
            }.attach()
        }
    }

    override fun onReExecute() {

    }

    override fun initUiListener() {
        viewBinding?.run {

        }
    }

    override fun bindViewModel(): MyConsultationFragmentVM {
        return ViewModelProvider(this, viewModelFactory)[MyConsultationFragmentVM::class.java]
    }

    override fun initMenu(): Int = 0
}
