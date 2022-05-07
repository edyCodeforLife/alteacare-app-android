package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep


data class InterfaceConfigOverwrite(
    val aPPNAME: String?,
    val dEFAULTREMOTEDISPLAYNAME: String?,
    val sETTINGSSECTIONS: List<Any>?,
    val sHOWCHROMEEXTENSIONBANNER: Boolean?,
    val sHOWPROMOTIONALCLOSEPAGE: Boolean?,
    val tOOLBARBUTTONS: List<Any>?
)