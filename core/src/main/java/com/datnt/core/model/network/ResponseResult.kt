package com.datnt.core.model.network

sealed class ResponseResult<out R> {

    data class Success<out T>(val data: T) : ResponseResult<T>()
    data class Error<T>(val code : Int? = 0, val exception: String) : ResponseResult<T>()
    object Loading : ResponseResult<Nothing>()

}


/**
 * `true` if [ResponseResult] is of type [Success] & holds non-null [Success.data].
 */
val ResponseResult<*>.succeeded
    get() = this is ResponseResult.Success && data != null

