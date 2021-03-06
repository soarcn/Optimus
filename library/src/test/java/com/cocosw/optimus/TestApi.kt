package com.cocosw.optimus

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TestApi {

    @GET("/get")
    fun get(): Single<User>

    @GET("/find/{id}")
    fun find(@Query("id") id: Int): Single<User>

    @POST("/post")
    fun post(): Call<List<User>>

    fun new(): Completable

    @GET("/call")
    fun call(): Call<Response<User>>
}

interface AnotherApi {
    @GET("/test")
    fun test(): Call<User>
}

class User(
    val id: Int,
    val email: String,
    val first_name: String,
    val last_name: String,
    val avatar: String
)

class TestMockUser : MockResponse {
    @Default
    val Happy = success { mockUser }
    val Another = success { mockUser(3) }
    val Empty = error { mockError }
    val HTTP403 = error(code = 403)
}

class TestFindUser : MockResponse {
    @Default
    val User = success { (id: Int) -> mockUser(id) }
    val Empty = error { mockError }
}

class TestMockUserList : MockResponse {
    @Default
    val Happy = success { listOf(mockUser) }
    val Empty = error { mockError }
}

val mockUser = User(1, "test", "test", "test", "test")

val mockError = ApiError(400, "error")

private fun mockUser(id: Int): User {
    return User(id, "test", "test", "test", "test")
}

class ApiError(val statusCode: Int, val error: String)
