package dev.sunnat629.barcodescan.api

import dev.sunnat629.barcodescan.network.NetworkResult
import retrofit2.Response

open class BaseRepository {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): NetworkResult<T> {
        return if (call.invoke().isSuccessful) {
            NetworkResult.Success(call.invoke().body()!!)
        }
        else {
            NetworkResult.Error("${call.invoke().code()}: ${call.invoke().message()}")
        }
    }
}