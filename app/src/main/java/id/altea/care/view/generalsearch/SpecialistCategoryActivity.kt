package id.altea.care.view.generalsearch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Specialization
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.isHtmlText
import id.altea.care.databinding.ActivitySpecialistCategoryBinding
import id.altea.care.view.generalsearch.item.SpecialistCategoryItem
import id.altea.care.view.specialist.model.SpecialistItemModel

class SpecialistCategoryActivity :
    BaseActivityVM<ActivitySpecialistCategoryBinding, GeneralSearchVM>() {

    private val keyword by lazy {
        intent?.getStringExtra(EXTRA_KEYWORD)
    }

    private val router by lazy { SpecialistCategoryRouter() }

    private val itemAdapter = ItemAdapter<IItem<*>>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.toolbar

    override fun getUiBinding(): ActivitySpecialistCategoryBinding {
        return ActivitySpecialistCategoryBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            rvSpecialistCategory.apply {
                layoutManager = GridLayoutManager(this@SpecialistCategoryActivity, 3)
                adapter = fastAdapter
            }
            baseViewModel?.getSpecialistCategory(keyword.orEmpty())
        }
    }

    override fun initUiListener() {
        fastAdapter.onClickListener = { _, _, item, _ ->
            if (item is SpecialistCategoryItem) {
                router.openSpecialist(
                    this, item.data.specializationId, keyword
                )
                baseViewModel?.sendEventSpecialistCategoryToAnalytics(item.data.specializationId, item.data.name)
            }
            false
        }
    }

    override fun observeViewModel(viewModel: GeneralSearchVM) {
        observe(viewModel.getSpecialist, ::handleGetSpecialist)
    }

    private fun handleGetSpecialist(specialistList: List<Specialization>?) {
        viewBinding?.run {
            txtInfoData.text = String.format(
                getString(R.string.str_info_data_show_from_one),
                specialistList?.size,
                getString(R.string.recomendation_specialis_for),
                keyword.orEmpty()
            ).isHtmlText()

            if (itemAdapter.adapterItemCount > 0) {
                itemAdapter.clear()
            }
            specialistList?.map { specialist ->
                itemAdapter.add(
                    SpecialistCategoryItem(
                        SpecialistItemModel(
                            specialist.icon?.url,
                            specialist.name.orEmpty(),
                            specialist.specializationId
                        )
                    )
                )
            }
        }
    }

    override fun bindViewModel(): GeneralSearchVM {
        return ViewModelProvider(this, viewModelFactory)[GeneralSearchVM::class.java]
    }

    companion object {
        private const val EXTRA_KEYWORD = "KEYWORD"
        fun createIntent(
            caller: Context,
            keyword: String
        ): Intent {
            return Intent(caller, SpecialistCategoryActivity::class.java)
                .putExtra(EXTRA_KEYWORD, keyword)
        }
    }
}