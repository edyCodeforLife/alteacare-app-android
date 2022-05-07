package id.altea.care.core.data.network.api

import id.altea.care.BuildConfig
import id.altea.care.core.data.request.RefreshTokenRequest
import id.altea.care.core.domain.cache.AccountCache
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.helper.util.Constant
import id.altea.care.core.helper.util.Constant.HEADER_APP_PLATFORM
import id.altea.care.core.helper.util.Constant.HEADER_APP_VERSION
import id.altea.care.core.helper.util.Constant.HEADER_APP_VERSION_CODE
import id.altea.care.core.helper.util.Constant.HEADER_AUTH
import id.altea.care.core.helper.util.Constant.HEADER_DEVICE_ID
import id.altea.care.core.helper.util.Constant.VALUE_PLATFORM
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AlteaCareInterceptor @Inject constructor(
    private val authCache: AuthCache,
    private val accountCache: AccountCache
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        fun getRequestHeader(chain: Interceptor.Chain): Request {
            return chain.request()
                .newBuilder()
                .apply {
                    if (authCache.getToken().isNotEmpty()) {
                        addHeader(HEADER_AUTH, "Bearer ${authCache.getToken()}")
                    }
                    if (authCache.getDeviceId().isNotEmpty()){
                        addHeader(HEADER_DEVICE_ID,authCache.getDeviceId())
                    }
                    addHeader(HEADER_APP_PLATFORM,VALUE_PLATFORM)
                    addHeader(HEADER_APP_VERSION_CODE,BuildConfig.VERSION_CODE.toString())
                    addHeader(HEADER_APP_VERSION,BuildConfig.VERSION_NAME)
                }.build()
        }

        val originRequest: Request = getRequestHeader(chain)
        val originResponse: Response = chain.proceed(originRequest)

        if (originResponse.code == 401) {
            synchronized(this) {
                if (doRefreshToken()) {
                    return chain.proceed(getRequestHeader(chain))
                }
            }
        }
        return originResponse
    }

    private fun doRefreshToken(): Boolean = try {
        val refreshTokenApi = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .apply {
                        addInterceptor(HttpLoggingInterceptor { message ->
                            Timber.d("Refresh Token %s ", message)
                        }.setLevel(HttpLoggingInterceptor.Level.BODY))
                    }.build()
            )
            .build()
            .create(RefreshTokenApi::class.java)

        val exec =
            refreshTokenApi.doRefreshToken(RefreshTokenRequest(authCache.getRefreshToken()))
                .execute()
        if (exec.isSuccessful) {
            exec.body()?.data?.let {
                authCache.setToken(it.accessToken.orEmpty())
                authCache.setRefreshToken(it.refreshToken.orEmpty())

                accountCache.updateToken(
                    authCache.getUserId(),
                    it.accessToken.orEmpty(),
                    it.refreshToken.orEmpty()
                )
            }
            true
        } else {
            accountCache.invalidateAccount(authCache.getUserId())
            authCache.invalidate()
            false
        }
    } catch (ex: IOException) {
        ex.printStackTrace()
        false
    } catch (ex: NullPointerException) {
        ex.printStackTrace()
        false
    }
}

