package id.altea.care.view.specialist

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.SpecialistCategoryPayloadBuilder
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.model.Specialization
import id.altea.care.core.ext.observe
import id.altea.care.databinding.FragmentSpecialistBinding
import id.altea.care.view.common.item.SpecialistItem
import id.altea.care.view.specialist.model.SpecialistItemModel
import kotlinx.android.synthetic.main.fragment_specialist.*
import javax.inject.Inject

class SpecialistFragment : BaseFragmentVM<FragmentSpecialistBinding, SpecialistFragmentVM>() {

    @Inject
    lateinit var analyticManager: AnalyticManager

    private val specialistItem by lazy { ItemAdapter<SpecialistItem>() }
    private val specialistAdapter by lazy { FastAdapter.with(specialistItem) }
    private val router by lazy { SpecialistFragmentRouter() }

    override fun observeViewModel(viewModel: SpecialistFragmentVM) {
        observe(viewModel.isLoadingLiveData, ::showLoading)
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.specialistResponse, ::handleSpecialistResponse)
    }

    override fun bindViewModel(): SpecialistFragmentVM {
        return ViewModelProvider(this, viewModelFactory)[SpecialistFragmentVM::class.java]
    }

    override fun getUiBinding(): FragmentSpecialistBinding {
        return FragmentSpecialistBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        initRecycle()
        viewModel?.getSpecialist()
    }

    private fun initRecycle() {
        viewBinding?.run {
            specialistRecycler.let {
                it.layoutManager = FlexboxLayoutManager(context).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.CENTER
                }
                it.adapter = specialistAdapter
            }
        }
    }

    private fun showLoading(isLoading: Boolean?) {
        specialistSwipe.isRefreshing = isLoading == true
    }

    private fun handleSpecialistResponse(listSpecialist: List<Specialization>?) {
        listSpecialist?.let { specilists ->
            specialistItem.clear()
            specialistItem.add(specilists.map {
                SpecialistItem(
                    SpecialistItemModel(
                        it.icon?.url,
                        it.name.orEmpty(),
                        it.specializationId
                    )
                )
            })
        }
    }

    override fun onReExecute() {}

    override fun initUiListener() {
        specialistAdapter.onClickListener = { _, _, item, _ ->
            router.openSpecialistSearch(requireContext(), item.specialist.specializationId ?: "","")
            sendEventSpecialistCategoryToAnalytics(item.specialist.specializationId, item.specialist.name)
            false
        }
        viewBinding?.run {
            specialistSwipe.setOnRefreshListener { viewModel?.getSpecialist() }
        }
    }

    private fun sendEventSpecialistCategoryToAnalytics(
        specialistCategoryId: String?,
        specialistCategoryName: String?
    ) {
        analyticManager.trackingEventSpecialistCategory(
            SpecialistCategoryPayloadBuilder(
                specialistCategoryId,
                specialistCategoryName
            )
        )
    }

    override fun initMenu(): Int = 0
    
}