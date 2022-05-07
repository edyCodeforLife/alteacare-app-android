package id.altea.care.core.domain.event

import id.altea.care.core.domain.model.RegisterInfo

data class CallBackEvent(val callBackEventCreated : Boolean = false,val regiterInfo : RegisterInfo)