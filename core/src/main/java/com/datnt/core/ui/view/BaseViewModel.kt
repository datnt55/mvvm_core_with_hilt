package com.qbicles.core.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel(){
    val loading = MutableLiveData<Boolean>()
    val message = MutableLiveData<String>()

    fun setLoading(loading: Boolean) {
        this.loading.value = loading
    }

}