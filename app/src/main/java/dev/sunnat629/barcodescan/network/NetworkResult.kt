package dev.sunnat629.barcodescan.network

sealed class NetworkResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : NetworkResult<T>()
    data class Error(val exception: String) : NetworkResult<Nothing>()
}