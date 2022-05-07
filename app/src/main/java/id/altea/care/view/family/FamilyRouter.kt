package id.altea.care.view.family

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import id.altea.care.core.domain.model.*
import id.altea.care.view.account.ChangePersonalBottomSheet
import id.altea.care.view.address.addressform.bottomsheet.AddressFormBottomSheet
import id.altea.care.view.address.addressform.bottomsheet.BottomSheetType
import id.altea.care.view.common.bottomsheet.FamilyContactBottomSheet
import id.altea.care.view.common.bottomsheet.GenderBottomSheet
import id.altea.care.view.consultation.ConsultationRouter
import id.altea.care.view.consultation.PageType
import id.altea.care.view.contact.ContactRouter
import id.altea.care.view.doctordetail.bottomsheet.FamilyMemberListBottomSheet
import id.altea.care.view.family.bottomsheet.DecisionFamilyBottomSheet
import id.altea.care.view.family.bottomsheet.FamilyFormBottomSheet
import id.altea.care.view.family.formfamily.FormFamilyActivity
import id.altea.care.view.family.bottomsheet.FamilyInfoBottomSheet
import id.altea.care.view.family.listfamily.bottomsheet.FamilyListOptionBottomSheet
import id.altea.care.view.family.detailfamily.DetailFamilyActivity
import id.altea.care.view.family.formfamily.FamilyFormType
import id.altea.care.view.register.contactinfo.RegisterContactRouter
import id.altea.care.view.register.contactinfo.RegisterContactState

class FamilyRouter {

    fun openFormBottomSheet(
        supportFragment: FragmentManager,
        data: SelectedModel?,
        listData: List<SelectedModel>?,
        submitCallback: (SelectedModel) -> Unit,
        dismissCallback: (() -> Unit) = {}
    ) {
        FamilyFormBottomSheet.newInstance(
            data,
            ArrayList(listData),
            submitCallback,
            dismissCallback
        ).show(supportFragment, "form_bottom")
    }

    fun openFamilyContactBottomSheet(
        caller: AppCompatActivity,
        familyContact: List<FamilyContact>?,
        submitCallback: (FamilyContact) -> Unit,
        dismissCallback: () -> Unit
    ) {
        FamilyContactBottomSheet.newInstance(familyContact,submitCallback, dismissCallback)
            .show(caller.supportFragmentManager, "gender")
    }

    fun openGenderBottomSheet(
        caller: AppCompatActivity,
        submitCallback: (String) -> Unit,
        dismissCallback: () -> Unit
    ) {
        GenderBottomSheet.newInstance(submitCallback, dismissCallback)
            .show(caller.supportFragmentManager, "gender")
    }


    fun openAddFamily(caller : AppCompatActivity, createLauncher: ActivityResultLauncher<Intent>){
        createLauncher.launch(FormFamilyActivity.createIntent(caller,null,FamilyFormType.CREATE))
    }

    fun openUpdateFamily(caller: AppCompatActivity,detailPatient: DetailPatient?,updateLauncher: ActivityResultLauncher<Intent>){
        updateLauncher.launch(FormFamilyActivity.createIntent(caller,detailPatient,FamilyFormType.UPDATE))
    }

    fun openDetailFamily(caller: AppCompatActivity,patientFamily : PatientFamily?,detailLauncher: ActivityResultLauncher<Intent>){
        detailLauncher.launch(DetailFamilyActivity.createIntent(caller,patientFamily))
    }

    fun openChangePersonalBottomSheet(
    caller: AppCompatActivity?,
    submitCallback: (String) -> Unit,
    dismissCallback: () -> Unit,
    state : ChangePersonalBottomSheet.State
    ) {
        caller?.let {
            ChangePersonalBottomSheet.newInstance(submitCallback, dismissCallback,state)
                    .show(it.supportFragmentManager, "change_personal")
        }
    }

    fun openContactActivity(source : Activity){
        source.startActivity(ContactRouter.createIntent(source))
    }


    fun openConfirmationBottomSheet(
            caller: AppCompatActivity,
            familyInfo: FamilyInfo,
            submitCallback: () -> Unit,
    ) {
        FamilyInfoBottomSheet.newInstance(
                familyInfo,
                submitCallback
        ).show(caller.supportFragmentManager, "confirmation")
    }


    fun openDecissionBottomSheet(
            caller: AppCompatActivity,
            submitAsAccount: () -> Unit,
            submitAsFamilyProfile : () -> Unit
    ){
      DecisionFamilyBottomSheet.newInstance(
              submitAsAccount,
              submitAsFamilyProfile).show(caller.supportFragmentManager,"decission")
    }

    fun openRegisterContactInfo(source: AppCompatActivity,familyInfo: FamilyInfo,registerLauncher : ActivityResultLauncher<Intent>) {
        registerLauncher.launch(RegisterContactRouter.createIntent(source, null,familyInfo, RegisterContactState.PAGE_FAMILY,null ))
    }

    fun openRegisterContactExistingInfo(source: AppCompatActivity,patientId : String?,registerLauncher : ActivityResultLauncher<Intent>) {
        registerLauncher.launch(RegisterContactRouter.createIntent(source, null,null, RegisterContactState.PAGE_FAMILY_EXISTING,patientId))
    }

    fun openBottomSheetOption(context: AppCompatActivity, idFamily: String, adapterPosition: Int) {
        FamilyListOptionBottomSheet.newInstance(idFamily, adapterPosition)
                .show(context.supportFragmentManager, "family_option")
    }

    fun openConsultationFragment(context: Context, pageType: PageType, patientFamily: PatientFamily?, consultation: Consultation?) {
        context.startActivity(ConsultationRouter.openConsultationFragment(context, pageType, patientFamily, consultation))
    }
}