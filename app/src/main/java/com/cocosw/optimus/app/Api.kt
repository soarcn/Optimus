package com.cocosw.optimus.app

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST

interface Api {
    @POST("api/login")
    fun login(username: String, password: String): Call<ResponseBody>
}

