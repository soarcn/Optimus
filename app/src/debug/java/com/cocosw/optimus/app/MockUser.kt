package com.cocosw.optimus.app

import com.cocosw.optimus.*

val mockApi = alter(Api::class.java,"Api") {
    Api::getUsers with MockUserList::class named "Get Users"
    Api::getUser with MockUser::class
    Api::addUser with MockUser::class
    Api::editUser with MockUser::class
}

class MockUser:MockResponse {
    @Default
    val Happy = success{mockUser}
    val Empty = notfound{}
}



class MockUserList:MockResponse {
    val Happy = success{listOf(mockUser)}
    val Empty = notfound{}
}

val mockUser = User(1, "test", "test", "test", "test")

val mockError = ApiError(400, "error")

private fun mockUser(id:Int): User {
    return User(id, "test", "test", "test", "test")
}