package id.altea.care.view.consultation

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.domain.model.Consultation
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.ext.showDialogBackConfirmationPayment
import id.altea.care.databinding.ActivityCreateConsultationBinding
import id.altea.care.view.consultation.ConsultationRouter.Companion.EXTRA_PAGE
import id.altea.care.view.consultation.ConsultationRouter.Companion.KEY
import id.altea.care.view.consultation.ConsultationRouter.Companion.KEY_CONSULTATION_DATA
import id.altea.care.view.consultation.confirm.ConfirmationFragment
import id.altea.care.view.consultation.payment.PaymentFragment
import kotlinx.android.synthetic.main.activity_create_consultation.*
import kotlinx.android.synthetic.main.line_steper_consultation.*
import kotlinx.android.synthetic.main.toolbar_default_center.view.*
import javax.inject.Inject


class ConsultationActivity : BaseActivity<ActivityCreateConsultationBinding>(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private val router by lazy { ConsultationRouter() }

    val categories = arrayOf("Buat Konsultasi", "Konfirmasi", "Bayar")
    var orderCode: String? = null
    var appointmentId: Int? = null


    private val pageType by lazy {
        intent.getSerializableExtra(EXTRA_PAGE) as PageType
    }

    val dataConsultation: Consultation? by lazy {
        intent.getParcelableExtra(KEY_CONSULTATION_DATA)
    }

    val patientFamily: PatientFamily? by lazy {
        intent.getParcelableExtra(KEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityCreateConsultationBinding =
        ActivityCreateConsultationBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding.run {
            initViewPager()
            appbar.txtToolbarTitle.text = getString(R.string.create_consultation_text)
            val extraAppointmentId = intent.getIntExtra(ConsultationRouter.EXTRA_APPOINTMENT, 0)
            val extraOrderCode = intent.getStringExtra(ConsultationRouter.EXTRA_ORDER_CODE)
            if (extraAppointmentId != 0) {
                appointmentId = extraAppointmentId
            }
            extraOrderCode?.let { orderCode = it }

        }

        when (pageType) {
            PageType.PAYMENT -> {
                vp_consultation.currentItem = 2
            }
            PageType.CONSULTATION -> {
                vp_consultation.currentItem = 0
            }
        }

    }

    private fun initViewPager() {
        viewBinding.run {
            vp_consultation.isUserInputEnabled = false
            vp_consultation.adapter = PagerAdapter(this@ConsultationActivity)
            vp_consultation.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    viewBinding.run {
                        if (position == 0) {
                            appbar.txtToolbarTitle.text = getString(R.string.create_consultation)
                            step1.text = getString(R.string.str_step_one)
                            textStep1.text = getString(R.string.str_choose_specialist)
                            step2.text = getString(R.string.str_step_two)
                            textStep2.text = getString(R.string.str_create_consultation)
                            step3.text = getString(R.string.str_step_three)
                            textStep3.text = getString(R.string.str_confirmation)
                        } else if (position == 1) {
                            appbar.txtToolbarTitle.text = getString(R.string.konsultasi_review)
                            step1.text = getString(R.string.str_step_two)
                            textStep1.text = getString(R.string.str_create_consultation)
                            step2.text = getString(R.string.str_step_three)
                            textStep2.text = getString(R.string.str_confirmation)
                            step3.text = getString(R.string.str_step_four)
                            textStep3.text = getString(R.string.str_payment)
                        } else if (position == 2) {
                            appbar.txtToolbarTitle.text = getString(R.string.payment_string)
                            step1.text = getString(R.string.str_step_three)
                            textStep1.text = getString(R.string.str_confirmation)
                            step2.text = getString(R.string.str_step_four)
                            textStep2.text = getString(R.string.str_payment)
                            step3.text = getString(R.string.str_step_five)
                            textStep3.text = getString(R.string.str_consultation)
                        }
                    }
                }
            })
        }
    }

    override fun initUiListener() {

    }

    override fun onBackPressed() {
        if (vp_consultation.currentItem == 2) {
            showDialogBackConfirmationPayment(this) {
                router.openHome(this)
            }
            return
        }

        if (vp_consultation.currentItem > 0) {
            vp_consultation.currentItem = vp_consultation.currentItem - 1
            return
        }
        super.onBackPressed()
    }


    inner class PagerAdapter constructor(fm: FragmentActivity) : FragmentStateAdapter(fm) {


        override fun getItemCount(): Int {
            return categories.size
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ConsultationFragment()
                1 -> ConfirmationFragment()
                2 -> PaymentFragment.newInstance(appointmentId ?: 0)
                else -> ConsultationFragment()
            }
        }

    }

    fun onNext() {
        viewBinding.run {
            vp_consultation.setCurrentItem(getItem(+1), true)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    private fun getItem(i: Int): Int {
        return vp_consultation.currentItem + i
    }


}