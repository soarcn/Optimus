package com.cocosw.optimus.app

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.POST

interface Api {
    @POST("api/users?page=2")
    fun login(username: String, password: String): Single<ResponseBody>
}

