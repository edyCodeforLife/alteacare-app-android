package id.altea.care.core.domain.model

import com.google.gson.annotations.SerializedName

data class ResultToken<T>(
                  val status: String?,
                  val message : String?,
                  val data : T)

data class Token( val expireAt: String?,
                  val identity: String?,
                  val roomCode: String?,
                  val token: String?)


