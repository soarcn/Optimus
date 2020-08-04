package com.cocosw.optimus.app

import retrofit2.Call
import retrofit2.http.POST

interface Api {
    @POST("api/login")
    fun login(): Call<Void>
}

