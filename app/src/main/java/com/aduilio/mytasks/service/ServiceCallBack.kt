package com.aduilio.mytasks.service

import androidx.lifecycle.MutableLiveData
import com.aduilio.mytasks.repository.ResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceCallBack<T>(private val liveData: MutableLiveData<ResponseDto<T>>) : Callback<T> {

    override fun onResponse(call: Call<T?>, response: Response<T?>) {
        if (response.isSuccessful) {
            liveData.value = ResponseDto(value = response.body())
        } else {
            liveData.value = ResponseDto(error = true)
        }
    }

    override fun onFailure(call: Call<T?>, t: Throwable) {
        liveData.value = ResponseDto(error = true)
    }
}