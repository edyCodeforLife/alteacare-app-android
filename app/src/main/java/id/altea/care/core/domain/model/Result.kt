package id.altea.care.core.domain.model

open class Result<T> (
    val status: Boolean?,
    val message : String?,
    val data : T
)

open class ResultMeta<T>(
    val meta: Meta,
    status: Boolean?,
    message: String?,
    data: T
) : Result<T>(status, message, data)