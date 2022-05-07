package id.altea.care.view.generalsearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.widget.textChanges
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.R
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.ParameterGeneralSearchPayloadBuilder
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.DataGeneralSearchButtonMore
import id.altea.care.core.domain.model.GeneralSearch
import id.altea.care.core.domain.model.TypeButton
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.databinding.ActivitySearchGeneralBinding
import id.altea.care.view.generalsearch.item.*
import id.altea.care.view.generalsearch.model.SearchContent
import kotlinx.android.synthetic.main.family_member_list_bottom_sheet.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GeneralSearchActivity : BaseActivityVM<ActivitySearchGeneralBinding, GeneralSearchVM>() {

    @Inject
    lateinit var analyticManager: AnalyticManager

    private val itemAdapter = ItemAdapter<IItem<*>>()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private val router by lazy { GeneralSearchRouter() }
    private val queryIntent by lazy { intent.getStringExtra(EXTRA_QUERY) }

    override fun bindViewModel(): GeneralSearchVM {
        return ViewModelProvider( this.viewModelStore,viewModelFactory).get(GeneralSearchVM::class.java)
    }

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivitySearchGeneralBinding {
        return ActivitySearchGeneralBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            generalRecyclerView.let { recyclerview ->
                recyclerview.layoutManager = LinearLayoutManager(this@GeneralSearchActivity)
                recyclerview.adapter = fastAdapter
            }

            if (queryIntent?.isNotEmpty() == true) {
                appbar.edtxLoginPassword.setText(queryIntent)
                baseViewModel?.getGeneralSearch(queryIntent.orEmpty())
            } else {
                baseViewModel?.getPopularSearch()
                appbar.edtxLoginPassword.requestFocus()
            }
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            appbar.edtxLoginPassword
                .textChanges()
                .skipInitialValue()
                .debounce(800, TimeUnit.MILLISECONDS)
                .subscribe { searchResult ->
                    if (searchResult.isEmpty()) {
                        baseViewModel?.getPopularSearch()
                    } else {
                        baseViewModel?.getGeneralSearch(searchResult.toString())
                    }
                    Timber.d("result -> $searchResult")
                    if (!searchResult.isNullOrEmpty()) {
                        analyticManager.trackingEventParameterGeneralSearch(
                            ParameterGeneralSearchPayloadBuilder(searchResult.toString())
                        )
                    }
                }.disposedBy(disposable)
        }

        fastAdapter.onClickListener = { _, _, item, _ ->
            when (item) {
                is GeneralSearchContentItem -> {
                    if (item.searchContent.typeContent == TypeContent.SYMTOM) {
                        baseViewModel?.sendEventSearchResultToAnalytics(
                            null,
                            null,
                            item.searchContent.content
                        )
                        router.openSpecialist(
                            this,
                            null,
                            if (item.searchContent.content.isNotEmpty()) {
                                item.searchContent.content
                            } else {
                                viewBinding?.appbar?.edtxLoginPassword?.text.toString()
                            }
                        )
                    } else {
                        router.openSpecialist(
                            this,
                            item.searchContent.id,
                            viewBinding?.appbar?.edtxLoginPassword?.text.toString()
                        )
                        baseViewModel?.sendEventSearchResultToAnalytics(
                            item.searchContent.content,
                            null,
                            null
                        )
                    }
                }
                is GeneralSearchDoctorItem -> {
                    router.openDoctorDetail(this, item.doctor)
                    baseViewModel?.sendEventSearchResultToAnalytics(
                        item.doctor.specialization?.name,
                        item.doctor.name,
                        null
                    )
                }

                is GeneralSearchItemButtonMore -> {
                    val data = item.dataGeneralSearchButtonMore
                    when (data.typeButton) {
                        TypeButton.SYMPTOM -> {
                            router.openSymptomList(
                                source = this,
                                keyword = viewBinding?.appbar?.edtxLoginPassword?.text.toString()
                            )
                        }
                        TypeButton.SPECIALIST_CATEGORY -> {
                            router.openSpecialistCategory(
                                source = this,
                                keyword = viewBinding?.appbar?.edtxLoginPassword?.text.toString()
                            )
                        }
                        TypeButton.DOCTOR -> {
                            router.openSpecialist(
                                source = this,
                                id = "",
                                query = viewBinding?.appbar?.edtxLoginPassword?.text.toString()
                            )
                        }
                    }
                }
            }
            true
        }
    }

    override fun observeViewModel(viewModel: GeneralSearchVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleBar)
        observe(viewModel.generalSearchResult, ::handleSearchResult)
    }

    private fun handleBar(showLoading: Boolean?) {
        viewBinding?.generalSearchProgressBar?.isVisible = showLoading ?: false
    }

    private fun handleSearchResult(generalSearch: GeneralSearch?) {
        if (itemAdapter.adapterItemCount > 0) {
            itemAdapter.clear()
        }
        generalSearch?.run {
            if (symtom.isNotEmpty()) {
                itemAdapter.add(GeneralSearchCategoryItem(getString(R.string.symptom)))
                itemAdapter.add(symtom.map { dataSymptom ->
                    GeneralSearchContentItem(
                        SearchContent(
                            id = dataSymptom.symtomId,
                            content = dataSymptom.symtomName,
                            typeContent = TypeContent.SYMTOM
                        )
                    )
                })
                if (meta.totalSymptom > 5 && viewBinding?.appbar?.edtxLoginPassword?.text?.isNotEmpty() == true) {
                    itemAdapter.add(
                        GeneralSearchItemButtonMore(
                            DataGeneralSearchButtonMore(
                                typeButton = TypeButton.SYMPTOM,
                                textButton = getString(R.string.text_button_symptom_more),
                                searchContent = symtom.map { dataSymptom ->
                                    SearchContent(
                                        id = dataSymptom.symtomId,
                                        content = dataSymptom.symtomName,
                                        typeContent = TypeContent.SYMTOM
                                    )
                                }
                            )
                        ))
                }
            }

            if (specialization.isNotEmpty()) {
                itemAdapter.add(GeneralSearchCategoryItem(getString(R.string.doctor_specialist)))
                itemAdapter.add(specialization.map { dataSpecialist ->
                    GeneralSearchContentItem(
                        SearchContent(
                            id = dataSpecialist.specializationId,
                            content = dataSpecialist.specializationName,
                            typeContent = TypeContent.SPECIALIST,
                            icon = dataSpecialist.specializationIcon
                        )
                    )
                })
                if (meta.totalSpecialization > 5 && viewBinding?.appbar?.edtxLoginPassword?.text?.isNotEmpty() == true) {
                    itemAdapter.add(
                        GeneralSearchItemButtonMore(
                            DataGeneralSearchButtonMore(
                                typeButton = TypeButton.SPECIALIST_CATEGORY,
                                textButton = getString(R.string.text_button_doctor_specialist_more),
                                searchContent = specialization.map { dataSpecialist ->
                                    SearchContent(
                                        id = dataSpecialist.specializationId,
                                        content = dataSpecialist.specializationName,
                                        typeContent = TypeContent.SPECIALIST,
                                        icon = dataSpecialist.specializationIcon
                                    )
                                }
                            )
                        ))
                }
            }

            if (doctor.isNotEmpty()) {
                itemAdapter.add(GeneralSearchCategoryItem(getString(R.string.doctor)))
                itemAdapter.add(doctor.map { dataDoctor -> GeneralSearchDoctorItem(dataDoctor) })
                if (meta.totalDoctor > 5 && viewBinding?.appbar?.edtxLoginPassword?.text?.isNotEmpty() == true) {
                    itemAdapter.add(
                        GeneralSearchItemButtonMore(
                            DataGeneralSearchButtonMore(
                                typeButton = TypeButton.DOCTOR,
                                textButton = getString(R.string.text_button_doctor_more),
                                dataDoctor = doctor
                            )
                        )
                    )
                }
            }
        }
    }

    companion object {
        private const val EXTRA_QUERY = "GeneralSearch.querySearch"
        fun createIntent(caller: Context, query: String = ""): Intent {
            return Intent(caller, GeneralSearchActivity::class.java)
                .putExtra(EXTRA_QUERY, query)
        }
    }
}

