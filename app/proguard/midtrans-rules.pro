# Also you must note that if you are using GSON for conversion from JSON to POJO representation, you must ignore those POJO classes from being obfuscated.
# Here include the POJO's that have you have created for mapping JSON response to POJO for example.
#-keep class com.midtrans.sdk.activities.**{ *;}
-keep class com.midtrans.sdk.analytics.** {*;}
-keep class com.midtrans.sdk.uikit.fragments.**{*;}
-keep class com.midtrans.sdk.corekit.models.** { *; }
-keep class com.midtrans.sdk.uikit.scancard.** { *; }
-keep class com.midtrans.sdk.corekit.eventbus.**{*;}
-keep class com.midtrans.sdk.corekit.analytics.MixpanelEvent{*;}
-keep class com.midtrans.sdk.corekit.analytics.MixpanelProperties{*;}
-keep class com.midtrans.sdk.corekit.core.MidtransSDK {*;}
-keep class com.midtrans.sdk.corekit.core.TransactionRequest {*;}
-keep class com.midtrans.sdk.corekit.core.Logger {*;}
-keep class com.midtrans.sdk.corekit.core.Constants {*;}
-keep class com.midtrans.sdk.corekit.core.SdkUtil {*;}
-keep class com.midtrans.sdk.corekit.core.PaymentType {*;}
-keep class com.midtrans.sdk.corekit.core.SdkCoreFlowBuilder {*;}
-keep class com.midtrans.sdk.corekit.core.BaseSdkBuilder {*;}
-keep class com.midtrans.sdk.corekit.core.ISdkFlow {*;}
-keep class com.midtrans.sdk.corekit.core.IScanner {*;}
-keep class com.midtrans.sdk.corekit.core.Currency {*;}
-keep class com.midtrans.sdk.corekit.core.UIKitCustomSetting {*;}
-keep class com.midtrans.sdk.corekit.core.themes.BaseColorTheme {*;}
-keep class com.midtrans.sdk.corekit.core.themes.ColorTheme {*;}
-keep class com.midtrans.sdk.corekit.core.themes.CustomColorTheme {*;}
-keep class com.midtrans.sdk.corekit.utilities.** {*;}
-keep class com.midtrans.sdk.uikit.utilities.SdkuikitUtil {*;}
-keep class com.midtrans.sdk.uikit.SdkuikitBuilder {*;}
-keep class com.midtrans.sdk.uikit.uikit {*;}
-keep class com.midtrans.sdk.uikit.scancard.ExternalScanner{*;}
-keep class com.midtrans.sdk.corekit.core.PaymentMethods{*;}
-keep class com.midtrans.sdk.corekit.callback.** { *; }
-keep class com.midtrans.sdk.corekit.core.PaymentMethod{*;}
-keep class com.midtrans.sdk.corekit.core.PaymentException{*;}

# Don't warn for missing support classes
-dontwarn org.greenrobot.eventbus.util.*$Support
-dontwarn org.greenrobot.eventbus.util.*$SupportManagerFragment

# Flurry
-keep class com.flurry.** { *; }
-dontwarn com.flurry.**
-keepattributes *Annotation*,EnclosingMethod
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Google Play Services library
-keep class * extends java.util.ListResourceBundle {
protected Object[][] getContents(); }

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
public static final *** NULL; }

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
@com.google.android.gms.common.annotation.KeepName *; }

# keep all class midtrans
-keep class com.midtrans.sdk.** {*;}
