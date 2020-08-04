Optimus
============

Optimus is a dynamic mock tool for retrofit

![Sample](https://github.com/soarcn/Optimus/blob/master/optimus.gif?raw=true)


Usage
=======

Given you have a service interface like this

```kotlin
interface Api {
    @POST("api/login")
    fun login(): Call<Void>
}
```

Step 1 Define mock data
------

```kotlin
class MockUser : MockResponse {
    @Default
    val HTTP200 = success {}
    val HTTP401 = error(401,"UnAuthorized") { ApiError(401, "Unauthorized") }
}
```

Step 2 Create an optimus instance
-------

```kotlin
val supplier = MockResponseSupplier.create(sharedpreference)
Optimus.Builder(supplier)
                    .retrofit(retrofit,sharedpreference)
                    .mockGraph(
                               alter(Api::class.java, "Api") {
                                   Api::login with MockUser::class named "Login"
                               })
                    .converter(Converter.create(moshi))
                    .build()
```

Step 3 Replace retrofit with optimus
-------

```kotlin
optimus.create(Api::class.java)
```

Optimus provides a view to change mock behavior in runtime, You may use it in a Alert like this

```kotlin
            AlertDialog.Builder(this)
                .setView(OptimusView(this).apply { this.setOptimus(optimus) })
                .create().show()
```

Testing
========

Optimus makes unittest and UI test easier.

Step1 User InMemory MockResponseSupplier and mockretrofit
----------
```kotlin
val supplier = MockResponseSupplier.memory()

Optimus.Builder(supplier)
       .retrofit(retrofit)
       .mockGraph(mockgraph)
       .build()
```

Step2 Change mock response with Api

```kotlin
        val api = optimus.create(Api::class.java)
        mockResponseSupplier.set(Api::call, MockUser::HTTP401)
        assert(api.login().response.code,401)
```

