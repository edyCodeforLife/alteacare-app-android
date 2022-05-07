package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.InterfaceConfigOverwrite

@Keep
data class InterfaceConfigOverwriteResponse(
    @SerializedName("APP_NAME")
    val aPPNAME: String?,
    @SerializedName("DEFAULT_REMOTE_DISPLAY_NAME")
    val dEFAULTREMOTEDISPLAYNAME: String?,
    @SerializedName("SETTINGS_SECTIONS")
    val sETTINGSSECTIONS: List<Any>?,
    @SerializedName("SHOW_CHROME_EXTENSION_BANNER")
    val sHOWCHROMEEXTENSIONBANNER: Boolean?,
    @SerializedName("SHOW_PROMOTIONAL_CLOSE_PAGE")
    val sHOWPROMOTIONALCLOSEPAGE: Boolean?,
    @SerializedName("TOOLBAR_BUTTONS")
    val tOOLBARBUTTONS: List<Any>?
){
    fun toInterfaceConfigOverwrite() : InterfaceConfigOverwrite{
        return InterfaceConfigOverwrite(
            aPPNAME,
            dEFAULTREMOTEDISPLAYNAME,
            sETTINGSSECTIONS,
            sHOWCHROMEEXTENSIONBANNER,
            sHOWPROMOTIONALCLOSEPAGE,
            tOOLBARBUTTONS
        )
    }
}