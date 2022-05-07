package id.altea.care.view.changephonenumber.otpphone

import androidx.appcompat.app.AppCompatActivity
import id.altea.care.view.changephonenumber.ChangePhoneNumberActivity

class PhoneOtpRouter {

    fun openPhoneNumberActivity(source : AppCompatActivity,phoneNumber : String){
        source.startActivity(ChangePhoneNumberActivity.createIntent(source,phoneNumber, ""))
        source.finish()
    }

}