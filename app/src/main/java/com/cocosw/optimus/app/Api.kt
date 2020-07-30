package com.cocosw.optimus.app

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface Api {

    @GET("api/users?page=2")
    fun getUsers(): Single<List<User>>

    @GET("api/user?page=2")
    fun getUser(id: Int): Single<User>

    @POST("api/user")
    fun addUser(id: Int): Single<User>

    @PUT("api/user")
    fun editUser(id: Int): Single<User>
}

class User(
    val id: Int,
    val email: String,
    val first_name: String,
    val last_name: String,
    val avatar: String
)
