package id.altea.care.view.doctordetail.bottomsheet

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import id.altea.care.R
import id.altea.care.core.base.BaseBottomSheetVM
import id.altea.care.core.domain.event.ChangePatienCallbackEvent
import id.altea.care.core.domain.model.Consultation
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.observe
import id.altea.care.core.helper.RxBus
import id.altea.care.databinding.FamilyMemberListBottomSheetBinding
import id.altea.care.view.common.item.ItemGeneralErrorRetry
import id.altea.care.view.common.item.ItemLoadingLottie
import id.altea.care.view.common.item.ProgressBarItem
import id.altea.care.view.consultation.PageType
import id.altea.care.view.doctordetail.item.ItemFamilyMemberList
import id.altea.care.view.family.FamilyRouter
import id.altea.care.view.family.item.ItemFamilyEmpty
import id.altea.care.view.family.item.ItemListFamily
import id.altea.care.view.family.item.ItemTitleFamily
import id.altea.care.view.family.listfamily.ListFamilyVM

class FamilyMemberListBottomSheet : BaseBottomSheetVM<FamilyMemberListBottomSheetBinding, ListFamilyVM>() {

    companion object {

        private const val KEY_CONSULTATION_DATA = "KEY_CONSULTATION_DATA"
        private const val KEY_STATUS = "KEY_STATUS"

        fun newInstance(consultation: Consultation?, isShowing: Boolean) = FamilyMemberListBottomSheet().apply {
            val bundle = Bundle()
            bundle.putBoolean(KEY_STATUS, isShowing)
            bundle.putParcelable(KEY_CONSULTATION_DATA, consultation)
            arguments = bundle
        }
    }

    private var isShowing: Boolean? = false

    private var consultation: Consultation? = null

    private val router by lazy { FamilyRouter() }

    private val itemAdapter by lazy { GenericItemAdapter() }
    private val fastAdapter by lazy { GenericFastItemAdapter() }
    private lateinit var endlessScroll: EndlessRecyclerOnScrollListener

    override fun getUiBinding(): FamilyMemberListBottomSheetBinding {
        return FamilyMemberListBottomSheetBinding.inflate(cloneLayoutInflater)
    }

    private var createLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) viewModel?.getFamilyMember(1)
        }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {

        isShowing = arguments?.getBoolean(KEY_STATUS)

        consultation = arguments?.getParcelable(KEY_CONSULTATION_DATA)

        viewBinding?.addListFamilyNextButton?.setOnClickListener {
            router.openAddFamily(requireContext() as AppCompatActivity, createLauncher)
        }

        initRecyclerview()
        viewModel?.getFamilyMember(1)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener {
            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }
        return super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    }

    override fun bindViewModel(): ListFamilyVM {
        return ViewModelProvider(this, viewModelFactory)[ListFamilyVM::class.java]
    }

    private fun initRecyclerview() {
        viewBinding?.run {
            recyclerview.run {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = fastAdapter
            }

            fastAdapter.addAdapter(1, itemAdapter)

            endlessScroll = object : EndlessRecyclerOnScrollListener(itemAdapter) {
                override fun onLoadMore(currentPage: Int) {
                    endlessScroll.disable()
                    viewModel?.getFamilyMember(currentPage + 1)
                }
            }.apply { disable() }

            recyclerview.addOnScrollListener(endlessScroll)

        }

        fastAdapter.onClickListener = { _, _, item, _ ->
            when (item) {
                is ItemListFamily -> {
                    handleOnClickListener(isShowing, item.patient)
                }
                is  ItemFamilyMemberList -> {
                    handleOnClickListener(isShowing, item.patient)
                }
            }
            true
        }
    }

    private fun handleOnClickListener(isShowing: Boolean?, patientFamily: PatientFamily) {
        when (isShowing) {
            true -> {
                dismiss()
                RxBus.post(ChangePatienCallbackEvent(changePatienCallbackEventCreated = true, patientFamily))
            }
            else -> {
                router.openConsultationFragment(
                    requireContext(),
                    PageType.CONSULTATION,
                    patientFamily,
                    consultation
                )
            }
        }
    }

    override fun observeViewModel(viewModel: ListFamilyVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.patientListEvent, ::handleFamilyListEvent)
        observe(viewModel.isLoadingFirstEvent, ::handleLoadingFirstPage)
        observe(viewModel.isLoadingMoreEvent, ::handleLoadingMoreEvent)
        observe(viewModel.isNextPageAvailableEvent, ::handleNextPageAvailable)
    }

    private fun handleFamilyListEvent(list: List<PatientFamily>?) {
        if (!list.isNullOrEmpty()) {
            if (fastAdapter.itemCount > 0) {
                fastAdapter.remove(fastAdapter.itemCount - 1)
            }

            fastAdapter.add(ItemTitleFamily(getString(R.string.str_main_profile)))

            list.sortedByDescending { it.isDefault }.map { patientFamily ->
                if (patientFamily.isDefault == true) {
                    fastAdapter.add(ItemListFamily(patientFamily))
                }
            }

            fastAdapter.add(ItemTitleFamily(getString(R.string.str_other_profil)))

            list.sortedByDescending { it.isDefault }.map { patientFamily ->
                if (patientFamily.isDefault == false) {
                    fastAdapter.add(ItemFamilyMemberList(patientFamily))
                }
            }

        } else if (fastAdapter.itemCount == 0) {
            fastAdapter.add(ItemFamilyEmpty())
        }
    }

    private fun handleLoadingFirstPage(showLoading: Boolean?) {
        fastAdapter.clear()
        if (showLoading == true) {
            fastAdapter.add(ItemLoadingLottie())
        }
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

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ServerError, Failure.NetworkConnection -> {
                if (fastAdapter.itemCount == 0) fastAdapter.add(ItemGeneralErrorRetry {
                    viewModel?.getFamilyMember(1)
                })
            }
            else -> super.handleFailure(failure)
        }
    }

}