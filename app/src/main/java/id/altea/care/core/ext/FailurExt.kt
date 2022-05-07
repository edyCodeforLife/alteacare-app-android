package id.altea.care.core.ext

import com.google.gson.Gson
import id.altea.care.core.data.response.GeneralErrorResponse
import id.altea.care.core.exception.Failure
import io.sentry.Sentry
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

fun Throwable.getGeneralErrorServer(): Failure {
    Timber.e(this)
    return when (this) {
        is HttpException -> {
            try {
                val response = Gson().fromJson(
                    this.response()?.errorBody()?.charStream(),
                    GeneralErrorResponse::class.java
                )
                if (this.code() >= 500) {
                    Sentry.captureMessage(this.response().toString() ?: "-")
                    return Failure.ServerError("Terjadi kesalahan server, mohon coba kembali nanti")
                }
                if (this.code() == 401) {
                    return Failure.ExpiredSession
                }
                Failure.ServerError((response.message ?: "").toString())
            } catch (e: Exception) {
                Sentry.captureException(e)
                Failure.ServerError("Terjadi kesalahan, mohon coba kembali nanti")
            }
        }
        is SocketTimeoutException -> Failure.ServerError("Waktu tunggu berakhir, mohon coba kembali")
        is IOException -> Failure.NetworkConnection
        else -> Failure.ServerError("Terjadi kesalahan, mohon coba kembali nanti")
    }
}
