package id.altea.care.view.family.detailfamily

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.DetailPatient
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.*
import id.altea.care.databinding.ActivityDetailFamilyBinding
import id.altea.care.view.account.ChangePersonalBottomSheet
import id.altea.care.view.common.enums.StatusMember
import id.altea.care.view.common.item.ItemGeneralErrorRetry
import id.altea.care.view.family.FamilyRouter
import id.altea.care.view.family.item.ItemDetailFamily

class DetailFamilyActivity : BaseActivityVM<ActivityDetailFamilyBinding, DetailFamilyVM>() {

    private var patientFamily : PatientFamily? = null
    private val router by lazy { FamilyRouter() }
    private val itemAdapter by lazy { GenericItemAdapter() }
    private val fastAdapter by lazy { GenericFastItemAdapter() }
    private var detailPatient : DetailPatient? =null
    private var resultCode : Int =0


    override fun bindViewModel(): DetailFamilyVM = ViewModelProvider(this, viewModelFactory)[DetailFamilyVM::class.java]

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityDetailFamilyBinding = ActivityDetailFamilyBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        patientFamily = intent.getParcelableExtra(PATIENT_EXTRA_ID) as PatientFamily?
        baseViewModel?.getFamilyDetail(patientFamily?.id.orEmpty())
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.str_detail_profile_family)
            layoutDetailFamilyRecycleview.let {
                it.layoutManager = LinearLayoutManager(this@DetailFamilyActivity)
                it.adapter = fastAdapter
            }

        }

    }

    private fun onValidateUpdate(){
        when{
            patientFamily?.status == StatusMember.VERIFIED.name || patientFamily?.isDefault == true ->{
                router.openChangePersonalBottomSheet(this@DetailFamilyActivity,
                        submitCallback = {
                            router.openContactActivity(this@DetailFamilyActivity)
                        },
                        dismissCallback = {},ChangePersonalBottomSheet.State.FAMILY)
            }
            patientFamily?.status ==  StatusMember.NOT_VERIFIED.name || patientFamily?.isDefault == false -> {
                router.openUpdateFamily(this@DetailFamilyActivity,detailPatient,updateLauncher)
            }
        }
    }

    private fun onValidateDelete() {
        when{
            patientFamily?.status == StatusMember.VERIFIED.name || patientFamily?.isDefault == true ->{
                router.openChangePersonalBottomSheet(this@DetailFamilyActivity,
                        submitCallback = {
                            router.openContactActivity(this@DetailFamilyActivity)
                        },
                        dismissCallback = {},ChangePersonalBottomSheet.State.FAMILY)
            }
            patientFamily?.status ==  StatusMember.NOT_VERIFIED.name || patientFamily?.isDefault == false  -> {
                showDialogRemoveFamily(this@DetailFamilyActivity, onAcceptSelected = {
                    baseViewModel?.deleteFamilyDetail(patientFamily?.id.orEmpty())
                })
            }
        }
    }

    var updateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            resultCode = RESULT_UPDATE_USER
            detailPatient = it.data?.getParcelableExtra(PATIENT_EXTRA_ID)
            baseViewModel?.getFamilyDetail(detailPatient?.id.orEmpty())
            showSuccessSnackbar(getString(R.string.str_succes_update_family))
        }else if(it.resultCode == RESULT_UPDATE_USER){
            setResult(RESULT_UPDATE_USER)
            finish()
        }else{
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            detailFamilyContactDelete.onSingleClick().subscribe {
                onValidateDelete()
            }.disposedBy(disposable)

            detailFamilyBtnChange.onSingleClick().subscribe {
               onValidateUpdate()
            }.disposedBy(disposable)

            detailFamilyBtnRegisterAccount.onSingleClick().subscribe {
                router.openRegisterContactExistingInfo(this@DetailFamilyActivity,detailPatient?.id,updateLauncher)
            }.disposedBy(disposable)

        }


    }

    override fun onBackPressed() {
        setResult(resultCode)
        super.onBackPressed()
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ServerError, Failure.NetworkConnection -> {
                onFailure()
                if (fastAdapter.itemCount == 0) fastAdapter.add(ItemGeneralErrorRetry {
                    baseViewModel?.getFamilyDetail(patientFamily?.id.orEmpty())
                })
            }
            else -> {
                onFailure()
                super.handleFailure(failure)
            }
        }
    }

    override fun observeViewModel(viewModel: DetailFamilyVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.detailPatientEvent, ::handleDetailPatient)
        observe(viewModel.statusDeleteEvent, ::handleDeleteEvent)
    }

    private fun handleDeleteEvent(status : Boolean?) {
        status?.let {
            if (it) {
                showSuccessSnackbar(getString(R.string.str_delete_family_success)){
                    setResult(RESULT_UPDATE_USER)
                    finish()
                }
            }
        }
    }

    private fun handleDetailPatient(detailPatient: DetailPatient?) {
        detailPatient?.let {
            this.detailPatient = it
            onConnect()
            fastAdapter.clear()
            fastAdapter.add(ItemDetailFamily(this, it))
            viewBinding?.detailFamilyContactName?.text = it.familyRelationType?.name ?: "-"
            viewBinding?.detailFamilyBtnRegisterAccount?.isVisible = it.isRegistered != true
        }
    }

    private fun onFailure() {
        viewBinding?.linearLayout4?.isVisible = false
        viewBinding?.detailFamilyBtnChange?.isVisible = false
    }

    private fun onConnect() {
        viewBinding?.linearLayout4?.isVisible = true
        viewBinding?.detailFamilyBtnChange?.isVisible = true
    }

    companion object {
        const val PATIENT_EXTRA_ID = "DetailFamily.PatientExtraId"
        const val RESULT_UPDATE_USER = 5
        fun createIntent(caller: Context, patientFamily: PatientFamily?): Intent {
            return Intent(caller, DetailFamilyActivity::class.java).apply {
                putExtra(PATIENT_EXTRA_ID, patientFamily)
            }
        }
    }
}