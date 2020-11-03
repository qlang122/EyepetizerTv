package com.qlang.eyepetizer.mvvm

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    val exception: MutableLiveData<Throwable> = MutableLiveData()

    fun launchUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.Main) { block() }
    }
}