package id.altea.care.view.common.ui.searchselectable

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.widget.textChanges
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.getSelectExtension
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.*
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.helper.util.Constant.SEARCH_FILTER_COUNTRY
import id.altea.care.core.helper.util.Constant.SEARCH_FILTER_TYPE_MESSAGE
import id.altea.care.core.helper.util.Constant.SEARCH_FIlTER_HOSPITAL
import id.altea.care.core.helper.util.Constant.SEARCH_FIlTER_SPECIALIST
import id.altea.care.databinding.ActivitySearchSelectableBinding
import id.altea.care.databinding.ItemGeneralSelectableBinding
import id.altea.care.databinding.ItemGeneralSelectableRadioBinding
import id.altea.care.view.common.item.*
import java.util.concurrent.TimeUnit

class SearchSelectableActivity :
    BaseActivityVM<ActivitySearchSelectableBinding, SearchSelectableVM>() {

    private val viewModel by viewModels<SearchSelectableVM> { viewModelFactory }
    private val itemAdapter by lazy { ItemAdapter<IItem<*>>() }
    private val fastAdapter by lazy { FastAdapter.with(itemAdapter) }
    var selectExtension = fastAdapter.getSelectExtension()
    private lateinit var title: String
    private lateinit var content: List<String>
    private var resultFilter = ArrayList<ResultFilter>()

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivitySearchSelectableBinding =
        ActivitySearchSelectableBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        title = intent.getStringExtra(EXTRA_TITLE) ?: ""
        content = intent.getStringArrayListExtra(EXTRA_CONTENT)?.toList() ?: listOf()
        selectExtension.isSelectable = true

        when (title) {
            SEARCH_FIlTER_SPECIALIST -> {
                viewModel.getSpecialist()
            }
            SEARCH_FIlTER_HOSPITAL -> {
                viewModel.getHospital()
            }
            SEARCH_FILTER_COUNTRY -> {
                viewModel.getCountries("")
            }
            SEARCH_FILTER_TYPE_MESSAGE ->{
                viewModel.getMessageType()
                viewBinding?.textInputLayout?.isVisible = false
            }

        }
        viewBinding?.searchSelectTxtTitle?.text = title
        initRecyclerView()
    }

    private fun initRecyclerView() {
        viewBinding?.run {
            searchSelectRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@SearchSelectableActivity)
                adapter = fastAdapter
            }
        }

        fastAdapter.addEventHook(object : ClickEventHook<IItem<*>>() {

            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemGeneralSelectableBinding) {
                    return (viewHolder.binding as ItemGeneralSelectableBinding).itemGeneralCheckbox
                } else if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemGeneralSelectableRadioBinding) {
                    return (viewHolder.binding as ItemGeneralSelectableRadioBinding).itemGeneralRadio
                }
                return null
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<IItem<*>>,
                item: IItem<*>
            ) {
                if (item is SelectableSpecializationItem) {
                    if (checkValue(item.specialization)) {
                        resultFilter.remove(
                            ResultFilter(
                                item.specialization.specializationId,
                                item.specialization.name,
                                if(!content.isNullOrEmpty()) content.first() else ""
                            )
                        )
                    } else {
                        resultFilter.add(
                            ResultFilter(
                                item.specialization.specializationId,
                                item.specialization.name,
                                if(!content.isNullOrEmpty()) content.first() else ""
                            )
                        )
                        viewBinding?.seachSelectBtn?.visibility = View.VISIBLE
                    }

                } else if (item is SelectableCountryItem) {
                    val selections = selectExtension.selections
                    if (selections.isNotEmpty()) {
                        val selectedPosition = selections.iterator().next()
                        selectExtension.deselect()
                        fastAdapter.notifyItemChanged(selectedPosition)
                    }
                    selectExtension.select(position)
                    resultFilter.clear()
                    if (checkValue(item.country)) {
                        resultFilter.remove(
                            ResultFilter(
                                item.country.countryId,
                                item.country.name,
                                if(!content.isNullOrEmpty()) content.first() else ""
                            )
                        )
                    } else {
                        resultFilter.add(
                            ResultFilter(
                                item.country.countryId,
                                item.country.name,
                                if(!content.isNullOrEmpty()) content.first() else ""
                            )
                        )
                        viewBinding?.seachSelectBtn?.visibility = View.VISIBLE
                    }
                }else if (item is SelectableTypeMessageItem) {
                    val selections = selectExtension.selections
                    if (selections.isNotEmpty()) {
                        val selectedPosition = selections.iterator().next()
                        selectExtension.deselect()
                        fastAdapter.notifyItemChanged(selectedPosition)
                    }
                    selectExtension.select(position)
                    resultFilter.clear()
                    if (checkValue(item.typeMessage)) {
                        resultFilter.remove(
                            ResultFilter(
                                item.typeMessage.id,
                                item.typeMessage.name,
                                if(!content.isNullOrEmpty()) content.first() else ""
                            )
                        )
                    } else {
                        resultFilter.add(
                            ResultFilter(
                                item.typeMessage.id,
                                item.typeMessage.name,
                                if(!content.isNullOrEmpty()) content.first() else ""
                            )
                        )
                        viewBinding?.seachSelectBtn?.visibility = View.VISIBLE
                    }
                }

            }

        })


    }

    private fun <T> checkValue(data: T): Boolean {
        for (result in resultFilter) {
            when (data) {
                is Specialization -> {
                    if (result.id.equals(data.specializationId)) {
                        return true
                    }
                }
                is Country -> {
                    if (result.id.equals(data.countryId)) {
                        return true
                    }
                }
                is TypeMessage ->{
                    if (result.id.equals(data.id)){
                        return true
                    }
                }
            }

        }
        return false
    }

    override fun initUiListener() {
        viewBinding?.run {
            searchSelectImgClose.setOnClickListener {
                finish()
            }

            seachSelectBtn.setOnClickListener {
                setResult(Activity.RESULT_OK, intent.putExtra("data", resultFilter))
                finish()
            }

            searchSelectEdtxSearch
                .textChanges()
                .skipInitialValue()
                .debounce(800, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (it.isEmpty()) {
                    } else {
                        when (title) {
                            SEARCH_FIlTER_SPECIALIST -> {
                                viewModel?.searchSpecialist(it.toString())
                            }
                            SEARCH_FIlTER_HOSPITAL -> {
                                viewModel.searchHospital(it.toString())
                            }
                            SEARCH_FILTER_COUNTRY -> {
                                viewModel.getCountries(it.toString())
                            }

                        }

                    }
                }
                .disposedBy(disposable)
        }
    }

    companion object {
        private const val EXTRA_TITLE = "SeachSelectable.Title"
        private const val EXTRA_CONTENT = "SeachSelectable.Content"
        fun createIntent(context: Context, title: String, content: List<String>): Intent {
            return Intent(context, SearchSelectableActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
                putStringArrayListExtra(EXTRA_CONTENT, ArrayList(content))
            }
        }
    }

    override fun observeViewModel(viewModel: SearchSelectableVM) {
        observe(viewModel.isLoadingLiveData, :: handleLoading)
        observe(viewModel.failureLiveData, :: handleFailure)
        observe(viewModel.specialistEvent, ::getResultSpecialist)
        observe(viewModel.hospitalEvent, ::getResultHospital)
        observe(viewModel.registerEvent, ::getResultCountry)
        observe(viewModel.typeMessage, ::getResultTypeMessage)
    }


    private fun getResultTypeMessage(typeMessages : List<TypeMessage>?) {
        if (typeMessages?.size != 0) {
            itemAdapter.clear()
            for (typeMessage in typeMessages!!) {
                itemAdapter.add(SelectableTypeMessageItem(typeMessage))
            }
        }
    }

    private fun getResultCountry(countries: List<Country>?) {
        if (countries?.size != 0) {
            itemAdapter.clear()
            for (country in countries!!) {
                itemAdapter.add(SelectableCountryItem(country))
            }
        }
    }

    private fun getResultHospital(hospitals: List<HospitalResult>?) {
        if (hospitals?.size != 0) {
            itemAdapter.clear()
            for (hospital in hospitals!!) {
                itemAdapter.add(SelectableHospitallItem(hospital))
            }
        }
    }

    private fun getResultSpecialist(specializations: List<Specialization>?) {
        if (specializations?.size != 0) {
            itemAdapter.clear()
            for (specialization in specializations!!) {
                itemAdapter.add(SelectableSpecializationItem(specialization))
            }
        }

    }

    override fun bindViewModel(): SearchSelectableVM = viewModel
}
