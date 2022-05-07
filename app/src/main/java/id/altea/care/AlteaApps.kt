package id.altea.care

import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.stetho.Stetho
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.zendesk.logger.Logger
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.di.DaggerAppComponent
import io.sentry.android.core.SentryAndroid
import timber.log.Timber
import zendesk.core.AnonymousIdentity
import zendesk.core.Identity
import zendesk.core.Zendesk
import zendesk.support.Support
import javax.inject.Inject

/**
 * Created by trileksono on 01/03/21.
 */

class AlteaApps : DaggerApplication() {

    @Inject
    lateinit var analyticManager: AnalyticManager

    override fun onCreate() {
        super.onCreate()

        initSentry()
        initMidtrans()
        initZendesk()
        analyticManager.initialize()
        analyticManager.trackingLastOpenTime()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
            Logger.setLoggable(true)
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return String.format(
                        "(%s:%s)#%s",
                        element.fileName,
                        element.lineNumber,
                        element.methodName
                    )
                }
            })
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .application(this)
            .build()
            .apply { inject(this@AlteaApps) }
    }


    private fun initSentry() {
        SentryAndroid.init(this) { options ->
            options.sessionTrackingIntervalMillis = 60000
            options.dsn = BuildConfig.SENTRY_BASE_DSN
            options.tracesSampleRate = 1.0
            options.environment = BuildConfig.FLAVOR
        }
    }

    private fun initMidtrans() {
        SdkUIFlowBuilder.init()
            .setClientKey(BuildConfig.MIDTRANS_CLIENT_KEY)
            .setContext(this)
            .setMerchantBaseUrl(BuildConfig.MIDTRANS_BASE_URL)
            .enableLog(false)
            .setColorTheme(CustomColorTheme("#61C7B5", "#3E8CB9", "#87CDE9"))
            .setLanguage("id")
            .buildSDK()
    }

    private fun initZendesk() {
        Zendesk.INSTANCE.init(
            this, BuildConfig.ZENDEK_URL,
            BuildConfig.ZENDESK_APPLICATION_ID,
            BuildConfig.ZENDEK_OAUTH_CLIENT_ID
        )

        /**
         * If you want to use unique identity, please check this out
         * https://developer.zendesk.com/documentation/classic-web-widget-sdks/support-sdk/working-with-the-support-sdk/building-a-dedicated-jwt-endpoint-for-the-support-sdk/
         */
        Support.INSTANCE.init(Zendesk.INSTANCE)
    }

}