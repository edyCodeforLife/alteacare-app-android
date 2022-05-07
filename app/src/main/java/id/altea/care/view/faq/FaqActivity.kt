package id.altea.care.view.faq

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Faq
import id.altea.care.core.domain.model.FrequentlyAskedQuestion
import id.altea.care.core.ext.observe
import id.altea.care.databinding.ActivityFaqBinding
import id.altea.care.view.faq.item.FaqItem
import id.altea.care.view.faq.item.ItemExpandableFaq


class FaqActivity : BaseActivityVM<ActivityFaqBinding, FaqVM>() {

    private val ItemExpandableFaq: ItemAdapter<ItemExpandableFaq> by lazy {
        ItemAdapter<ItemExpandableFaq>()
    }

    private val frequentlyAskedQuestions = ArrayList<FrequentlyAskedQuestion>()

    private val viewModel by viewModels<FaqVM> { viewModelFactory }

    override fun observeViewModel(viewModel: FaqVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.faqEvent, :: getFaq)
    }

    private fun getFaq(faq: List<Faq>?) {
        faq?.let {
            for (faq in it) {
                frequentlyAskedQuestions.add(
                    FrequentlyAskedQuestion(
                        faq.faqId!!,
                        faq.question!!,
                        faq.answer!!
                    )
                )
            }
            renderList(frequentlyAskedQuestions)
        }
    }

    override fun bindViewModel(): FaqVM = viewModel

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityFaqBinding = ActivityFaqBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewModel.getFaq()
        viewBinding?.appbar?.txtToolbarTitle?.text = getString(R.string.str_faq)

        bindList()
    }

    override fun initUiListener() {

    }


    private fun renderList(faq: List<FrequentlyAskedQuestion>?) {
        ItemExpandableFaq.add(
            faq?.map {
                ItemExpandableFaq(it.question).also { item ->
                    item.subItems = mutableListOf(FaqItem(it.answer))
                }
            } ?: emptyList()
        )
    }

    private fun bindList() {
        viewBinding?.faqRecycler?.run {
            layoutManager = LinearLayoutManager(this@FaqActivity, RecyclerView.VERTICAL, false)
            adapter = FastAdapter.with(ItemExpandableFaq).also {
                it.getExpandableExtension()
            }
        }
    }

}