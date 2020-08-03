package com.cocosw.optimus.app

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.POST

interface Api {
    @POST("api/login")
    fun login(username: String, password: String): Call<Response<Void>>
}

