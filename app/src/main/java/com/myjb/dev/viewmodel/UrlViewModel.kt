package com.myjb.dev.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myjb.dev.model.Repository
import com.myjb.dev.model.data.METHOD
import com.myjb.dev.model.remote.APIResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class UrlViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _result = MutableLiveData<APIResponse>()
    val result: MutableLiveData<APIResponse>
        get() = _result

    fun getImageUrls(method: METHOD, text: String) {
        viewModelScope.launch(Dispatchers.Main) {
            if (text.isBlank()) {
                _result.value = APIResponse.Success(mutableListOf())
                return@launch
            }

            repository.getImageUrls(method = method, text = text).collect {
                _result.value = it
            }
        }
    }
}