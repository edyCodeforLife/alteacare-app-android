# issue cannot copy text on webview
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep class * extends android.webkit.WebChromeClient { *; }

-keep class id.altea.care.core.base.** {*;}

-keep class * extends android.app.Activity
-keep class * extends androidx.fragment.app.Fragment{}
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable

# keep all data class model
-keep class id.altea.care.core.data.response.** {*;}
-keep class id.altea.care.core.data.request.** {*;}
-keep class id.altea.care.core.domain.event.** {*;}
-keep class id.altea.care.core.domain.model.** {*;}
