package com.datnt.core.model.network

sealed class ResponseResultProgress<out R> {

    data class Success<out T>(val data: T) : ResponseResultProgress<T>()
    data class Progress<out T>(val id : Int, val progress: Int) : ResponseResultProgress<T>()
    data class Error<T>(val code : Int? = 0, val exception: String) : ResponseResultProgress<T>()
    object Loading : ResponseResultProgress<Nothing>()

}

