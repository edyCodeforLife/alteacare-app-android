package id.altea.care.view.account.setting

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.helper.NetworkHandler
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class SettingAccountVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler)