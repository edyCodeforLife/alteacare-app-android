package id.altea.care.core.data.network.api

import id.altea.care.core.data.request.RefreshTokenRequest
import id.altea.care.core.data.response.RefreshTokenResponse
import id.altea.care.core.data.response.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// special case, without injection
interface RefreshTokenApi {

    @POST("/user/auth/refresh-token")
    fun doRefreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Call<Response<RefreshTokenResponse>>
}
