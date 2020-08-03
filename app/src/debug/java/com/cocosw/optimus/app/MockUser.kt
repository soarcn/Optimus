package com.cocosw.optimus.app

import com.cocosw.optimus.Default
import com.cocosw.optimus.MockResponse
import com.cocosw.optimus.alter
import com.cocosw.optimus.error
import com.cocosw.optimus.success

val mockApi = alter(Api::class.java, "Api") {
    Api::login with MockUser::class named "Login"
}

class MockUser : MockResponse {
    @Default
    val HTTP200 = success {}
    val HTTP401 = error(code = 401) {  }
    val NOTFOUND = error { ApiError(404,"Can not find your account") }
    val LOCKED = error { ApiError(400,"Your account been locked") }
    val EXPIRED = error { ApiError(400,"Your account is expired, please contact us via email xxx@xx.com") }
}
